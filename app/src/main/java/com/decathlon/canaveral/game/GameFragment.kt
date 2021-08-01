package com.decathlon.canaveral.game

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.X01Player
import com.decathlon.canaveral.databinding.FragmentGameBinding
import com.decathlon.canaveral.game.adapter.KeyboardAdapter
import com.decathlon.canaveral.game.adapter.PlayerPointsAdapter
import com.decathlon.canaveral.game.adapter.PlayersWaitingAdapter
import com.decathlon.canaveral.game.dialog.GameScoreFragmentArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class GameFragment : Fragment() {

    private lateinit var args: GameActivityArgs
    private lateinit var _binding: FragmentGameBinding

    private val game01ViewModel: Game01ViewModel = get()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        _binding = FragmentGameBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent?.extras != null) {
            args = GameActivityArgs.fromBundle(activity?.intent?.extras!!)
        }

        val startingPoints = resources.getStringArray(R.array.zero_game_type_array)[args.variantIndex].toInt()
        val nbRounds = resources.getStringArray(R.array.game_01_detail_round)[args.roundIndex].toIntOrNull()

        // Options
        _binding.gameOptions.setOnClickListener {
            findNavController().navigate(R.id.action_game_to_options)
        }

        // Player points remaining
        _binding.playerPointsRemaining.text = startingPoints.toString()

        // Player darts points
        _binding.playerDartsPoints.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val playerPointsAdapter = PlayerPointsAdapter()
        _binding.playerDartsPoints.adapter = playerPointsAdapter
        playerPointsAdapter.setData(emptyList(), false)

        // Players waiting
        _binding.playersWaiting.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val playersWaitingAdapter = PlayersWaitingAdapter(startingPoints, args.isBull25)
        _binding.playersWaiting.adapter = playersWaitingAdapter

        // Keyboard
        val keyboardAdapter = KeyboardAdapter(
            view.context,
            { point -> game01ViewModel.addPlayerPoint(point) },
            { game01ViewModel.removeLastPlayerPoint() })
        _binding.keyboardDkt.adapter = keyboardAdapter

        // ViewModel observers
        game01ViewModel.currentPlayerLiveData.observe(viewLifecycleOwner, {
            _binding.playersWaiting.isVisible = (game01ViewModel.players.size > 1)
            _binding.playersWaitingSeparator.isVisible = (game01ViewModel.players.size > 1)

            _binding.playerName.text = it?.nickname
            _binding.playerRound.text = if (nbRounds == null) {
                resources.getString(R.string.player_round_unlimited, game01ViewModel.currentRound)
            } else {
                resources.getString(R.string.player_round, game01ViewModel.currentRound, nbRounds)
            }

            _binding.playerPointsRemaining.text = startingPoints
                    .minus(DartsUtils.getPlayerScore(args.isBull25, it, game01ViewModel.playersPoints))
                    .toString()

            // Other players ordered
            val otherPlayers = getWaitingPlayersOrdered(it, game01ViewModel.players)
            if (otherPlayers.isNotEmpty()) {
                playersWaitingAdapter.setData(otherPlayers, game01ViewModel.playersPoints)
            }
        })
        game01ViewModel.getCurrentPlayer()

        var jobNextPlayer: Job? = null
        game01ViewModel.playersPointsLivedata.observe(viewLifecycleOwner, {
            playerPointsAdapter.setData(
                DartsUtils.getPlayerRoundDarts(game01ViewModel.currentPlayer!!, game01ViewModel.currentRound, it),
                game01ViewModel.isRoundDecreasing
            )
            val remainingPoints = startingPoints.minus(DartsUtils.getPlayerScore(args.isBull25, game01ViewModel.currentPlayer!!, it))

            if ((_binding.playerPointsRemaining.text as String).toInt() != remainingPoints) {
                startScoreAnimation(
                    _binding.playerPointsRemaining,
                    (_binding.playerPointsRemaining.text as String).toInt(),
                    remainingPoints
                )
            }

            // Test if game is finished
            if (DartsUtils.is01GameFinished(startingPoints, nbRounds, game01ViewModel.players, it, args.isBull25)) {
                goToPlayersStatsScreen(startingPoints)
            }

            // Go to next player
            if (DartsUtils.isPlayerRoundComplete(game01ViewModel.currentPlayerLiveData.value!!, game01ViewModel.currentRound, it)) {
                jobNextPlayer = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    if (game01ViewModel.isStackIncreasing) {
                        delay(2000)
                        showPlayerRoundScore(
                            DartsUtils.getScoreFromPointList(
                                DartsUtils.getPlayerRoundDarts(
                            game01ViewModel.currentPlayerLiveData.value!!, game01ViewModel.currentRound, it), args.isBull25))
                    } else {
                        delay(3000)
                    }
                    playerPointsAdapter.setData(emptyList(), false)
                    game01ViewModel.selectNextPlayer()
                }
            } else if (!game01ViewModel.isStackIncreasing) {
                jobNextPlayer?.cancel()
            }
        })
        game01ViewModel.getPlayersPoints()
    }

    private fun goToPlayersStatsScreen(startingPoints: Int) {
        val x01PlayerList = emptyList<X01Player>().toMutableList()
        game01ViewModel.players.forEach {
            x01PlayerList.add(
                X01Player(
                    it.id,
                    it.nickname,
                    it.firstname,
                    it.lastname,
                    it.image,
                    remainingPoints = startingPoints.minus(
                        DartsUtils.getPlayerScore(args.isBull25, it, game01ViewModel.playersPoints)),
                    checkout = DartsUtils.getScoreFromPointList(
                        DartsUtils.getPlayerLastRoundDarts(it, game01ViewModel.playersPoints),
                        args.isBull25
                    ),
                    ppd = DartsUtils.getPlayerPPD(it, game01ViewModel.playersPoints)
            ))
        }
        lifecycleScope.launchWhenResumed {
            delay(1200)
            findNavController().navigate(R.id.action_game_to_end,
                GameEndStatsFragmentArgs(x01PlayerList.toTypedArray(), args.variantIndex, args.isBull25, args.roundIndex, args.inIndex, args.outIndex).toBundle())
        }
    }

    private fun getWaitingPlayersOrdered(
        it: Player,
        allPlayers: List<Player>
    ): ArrayList<Player> {
        val playersWaiting = ArrayList<Player>()
        for (player in allPlayers) {
            if (player != it) {
                if (allPlayers.indexOf(player) < allPlayers.indexOf(it))
                    playersWaiting.add(playersWaiting.size, player)
                else
                    playersWaiting.add(
                        allPlayers.indexOf(player)
                                - allPlayers.indexOf(it) - 1, player
                    )
            }
        }
        return playersWaiting
    }

    @SuppressLint("Recycle")
    private fun startScoreAnimation(textView: AppCompatTextView, start: Int, end: Int) {
        val valueAnimator = ValueAnimator.ofInt(start, end)
        valueAnimator.duration = 800
        valueAnimator.interpolator = LinearOutSlowInInterpolator()
        valueAnimator.addUpdateListener {
            textView.text = it.animatedValue.toString()
        }
        valueAnimator.start()
    }

    private fun showPlayerRoundScore(score: Int) {
        this.findNavController().navigate(
            R.id.action_game_to_score,
            GameScoreFragmentArgs(score).toBundle())
    }
}
