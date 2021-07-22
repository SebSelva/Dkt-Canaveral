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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.DartsUtils
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.databinding.FragmentGameBinding
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_DETAIL_IS_BULL_25
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_VARIANT
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_PLAYERS
import com.decathlon.canaveral.game.adapter.KeyboardAdapter
import com.decathlon.canaveral.game.adapter.PlayerPointsAdapter
import com.decathlon.canaveral.game.adapter.PlayersWaitingAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class GameFragment : Fragment() {

    private val game01ViewModel: Game01ViewModel = get()
    private lateinit var _binding: FragmentGameBinding

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

        val bundleReceived = activity?.intent?.extras
        val gameVariant: Int? = bundleReceived?.getInt(BUNDLE_KEY_GAME_VARIANT)
        val isBull25: Boolean? = bundleReceived?.getBoolean(BUNDLE_KEY_GAME_DETAIL_IS_BULL_25, true)
        val players: List<Player>? = bundleReceived?.getParcelableArrayList(BUNDLE_KEY_PLAYERS)

        if (players != null) {
            game01ViewModel.players = players
        } else {
            activity?.finish()
        }

        val startingPoints = resources.getStringArray(R.array.zero_game_type_array)[gameVariant!!].toInt()

        initializeViews(view, startingPoints, isBull25)
    }

    private fun initializeViews(
        view: View,
        startingPoints: Int,
        isBull25: Boolean?
    ) {
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
        val playersWaitingAdapter = PlayersWaitingAdapter(startingPoints, isBull25 == true)
        _binding.playersWaiting.adapter = playersWaitingAdapter

        _binding.playersWaiting.isVisible = game01ViewModel.players.size > 1
        _binding.playersWaitingSeparator.isVisible = game01ViewModel.players.size > 1

        // Keyboard
        val keyboardAdapter = KeyboardAdapter(
            view.context,
            { point -> game01ViewModel.addPlayerPoint(point) },
            { game01ViewModel.removeLastPlayerPoint() })
        _binding.keyboardDkt.adapter = keyboardAdapter

        // ViewModel observers
        game01ViewModel.currentPlayerLiveData.observe(viewLifecycleOwner, {
            _binding.playerName.text = it?.nickname
            _binding.playerRound.text =
                resources.getString(R.string.player_round, game01ViewModel.currentRound, Game01ViewModel.MAX_ROUNDS)
            _binding.playerPointsRemaining.text =
                startingPoints.minus(
                    DartsUtils.getPlayerScore(
                        isBull25 == true,
                        it,
                        game01ViewModel.playersPointsLivedata.value
                    )
                ).toString()

            // Other players ordered
            val otherPlayers = getWaitingPlayersOrdered(it, game01ViewModel.players)
            if (otherPlayers.isNotEmpty()) {
                playersWaitingAdapter.setData(
                    otherPlayers,
                    game01ViewModel.playersPointsLivedata.value
                )
            }
        })
        game01ViewModel.getCurrentPlayer()

        var job: Job? = null
        game01ViewModel.playersPointsLivedata.observe(viewLifecycleOwner, {
            playerPointsAdapter.setData(
                DartsUtils.getPlayerLastDarts(
                    game01ViewModel.currentPlayerLiveData.value!!, game01ViewModel.currentRound, it),
                !game01ViewModel.isStackIncreasing
            )
            startScoreAnimation(
                _binding.playerPointsRemaining,
                (_binding.playerPointsRemaining.text as String).toInt(),
                startingPoints.minus(
                    DartsUtils.getPlayerScore(
                        isBull25 == true,
                        game01ViewModel.currentPlayerLiveData.value!!,
                        it
                    )
                )
            )

            // Go to next player
            if (DartsUtils.isPlayerRoundComplete(game01ViewModel.currentPlayerLiveData.value!!, game01ViewModel.currentRound, it)) {
                job = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    delay(if (game01ViewModel.isStackIncreasing) 2200 else 3200)
                    if (game01ViewModel.isStackIncreasing) {
                        showPlayerRoundScore(DartsUtils.getScoreFromPointList(DartsUtils.getPlayerLastDarts(
                            game01ViewModel.currentPlayerLiveData.value!!, game01ViewModel.currentRound, it), isBull25!!))
                    }
                    playerPointsAdapter.setData(emptyList(), false)
                    game01ViewModel.selectNextPlayer()
                }
            } else if (!game01ViewModel.isStackIncreasing) {
                job?.cancel()
            }
        })
        game01ViewModel.getPlayersPoints()
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
        valueAnimator.duration = 1000
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
