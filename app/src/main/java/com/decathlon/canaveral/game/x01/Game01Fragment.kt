package com.decathlon.canaveral.game.x01

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.model.X01PlayerStats
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.canaveral.databinding.FragmentGameBinding
import com.decathlon.canaveral.game.GameEndStatsFragmentArgs
import com.decathlon.canaveral.game.adapter.KeyboardAdapter
import com.decathlon.canaveral.game.adapter.PlayerPointsAdapter
import com.decathlon.canaveral.game.adapter.PlayersWaitingAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Game01Fragment : BaseFragment<FragmentGameBinding>() {

    private val args: Game01FragmentArgs by navArgs()
    private val game01ViewModel by viewModel<Game01ViewModel>()
    override var layoutId = R.layout.fragment_game

    private lateinit var playerPointsAdapter: PlayerPointsAdapter
    private lateinit var playersWaitingAdapter: PlayersWaitingAdapter
    private var jobNextPlayer: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        game01ViewModel.startingPoints = resources.getStringArray(R.array.game_x01_variant_array)[args.variantIndex].toInt()
        game01ViewModel.isBull25 = args.isBull25
        game01ViewModel.inValue = args.inIndex
        game01ViewModel.outValue = args.outIndex
        game01ViewModel.nbRounds = resources.getStringArray(R.array.game_x01_detail_round)[args.roundIndex].toIntOrNull()

        initViews(view)
    }

    private fun initViews(view: View) {

        // Options
        _binding.gameOptions.setOnClickListener {
            findNavController().navigate(R.id.action_game_to_options)
        }

        // Player points remaining
        _binding.playerPointsRemaining.text = game01ViewModel.startingPoints.toString()

        val orientationLayout = if (resources.configuration.orientation == ORIENTATION_PORTRAIT) {
            LinearLayoutManager.HORIZONTAL
        } else {
            LinearLayoutManager.VERTICAL
        }
        // Player darts points
        _binding.playerDartsPoints.layoutManager =
            LinearLayoutManager(requireContext(), orientationLayout, false)
        playerPointsAdapter = PlayerPointsAdapter()
        _binding.playerDartsPoints.adapter = playerPointsAdapter
        playerPointsAdapter.setData(emptyList(), false)

        // Players waiting
        _binding.playersWaiting.layoutManager =
            LinearLayoutManager(requireContext(), orientationLayout, false)
        playersWaitingAdapter = PlayersWaitingAdapter(game01ViewModel.startingPoints, game01ViewModel.isBull25, game01ViewModel.inValue)
        _binding.playersWaiting.adapter = playersWaitingAdapter

        // Keyboard
        val keyboardAdapter = KeyboardAdapter(
            view.context,
            { point -> game01ViewModel.addPlayerPoint(point) },
            { game01ViewModel.removeLastPlayerPoint() })
        _binding.keyboardDkt.adapter = keyboardAdapter

        // ViewModel observers
        game01ViewModel.currentPlayerLiveData.observe(viewLifecycleOwner, {
            onUpdateCurrentPlayer(it, game01ViewModel.nbRounds, game01ViewModel.startingPoints, playersWaitingAdapter)
        })
        game01ViewModel.getCurrentPlayer()

        game01ViewModel.playersPointsLivedata.observe(viewLifecycleOwner, {
            onUpdatePlayersPoints(playerPointsAdapter, it, game01ViewModel.startingPoints, game01ViewModel.nbRounds)
        })
        game01ViewModel.getPlayersPoints()
    }

    private fun onUpdatePlayersPoints(
        playerPointsAdapter: PlayerPointsAdapter,
        stack: Stack<PlayerPoint>,
        startingPoints: Int,
        nbRounds: Int?
    ) {
        playerPointsAdapter.setData(
            DartsUtils.getPlayerRoundDarts(
                game01ViewModel.currentPlayer!!,
                game01ViewModel.currentRound,
                stack
            ),
            game01ViewModel.isRoundDecreasing
        )
        val remainingPoints = DartsUtils.get01GamePlayerScore(startingPoints,
            game01ViewModel.isBull25,
            game01ViewModel.currentPlayer!!,
            stack,
            game01ViewModel.inValue)

        _binding.playerPdd.text = String.format(Locale.ENGLISH, resources.getString(R.string.player_ppd),
        DartsUtils.getPlayerPPD(game01ViewModel.currentPlayer!!, stack, args.isBull25))

        if ((_binding.playerPointsRemaining.text as String).toInt() != remainingPoints) {
            startScoreAnimation(
                _binding.playerPointsRemaining,
                (_binding.playerPointsRemaining.text as String).toInt(),
                remainingPoints
            )
        }

        // Test if game is finished
        if (DartsUtils.is01GameFinished(startingPoints, game01ViewModel.isBull25, nbRounds, game01ViewModel.inValue, game01ViewModel.players, stack)) {
            goToPlayersStatsScreen(startingPoints)
        }

        // Go to next player
        if (DartsUtils.isPlayerRoundComplete(
                game01ViewModel.currentPlayerLiveData.value!!,
                game01ViewModel.currentRound,
                stack
            )
        ) {
            jobNextPlayer = viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                when {
                    game01ViewModel.isRoundBusted && game01ViewModel.isStackIncreasing-> {
                        delay(500)
                        showTransitionInfo(resources.getString(R.string.game_round_bust))
                        // Wait to score animation finish
                        delay(400)
                    }
                    game01ViewModel.isStackIncreasing -> {
                        delay(2000)
                        showTransitionInfo(
                            DartsUtils.getScoreFromPointList(
                                DartsUtils.getPlayerLastValidRoundDarts(
                                    args.inIndex,
                                    game01ViewModel.currentPlayer!!,
                                    stack
                                ), args.isBull25
                            ).toString()
                        )
                        delay(200)
                    }
                    else -> {
                        delay(3000)
                    }
                }
                playerPointsAdapter.setData(emptyList(), false)
                game01ViewModel.selectNextPlayer()
            }
        } else if (!game01ViewModel.isStackIncreasing) {
            jobNextPlayer?.cancel()
        }
    }

    private fun onUpdateCurrentPlayer(
        player: Player,
        nbRounds: Int?,
        startingPoints: Int,
        playersWaitingAdapter: PlayersWaitingAdapter
    ) {
        _binding.playersWaiting.isVisible = (game01ViewModel.players.size > 1)
        _binding.playersWaitingSeparator.isVisible = (game01ViewModel.players.size > 1)

        _binding.currentPlayer = player
        _binding.playerRound.text = if (nbRounds == null) {
            resources.getString(R.string.player_round_unlimited, game01ViewModel.currentRound)
        } else {
            resources.getString(R.string.player_round, game01ViewModel.currentRound, nbRounds)
        }
        _binding.playerPdd.text = String.format(Locale.ENGLISH, resources.getString(R.string.player_ppd),
            DartsUtils.getPlayerPPD(player, game01ViewModel.playersPoints, args.isBull25))

        _binding.playerPointsRemaining.text = startingPoints
            .minus(DartsUtils.getPlayerScore(args.isBull25, player, game01ViewModel.playersPoints, args.inIndex))
            .toString()

        // Other players ordered
        val otherPlayers = getWaitingPlayersOrdered(player, game01ViewModel.players)
        if (otherPlayers.isNotEmpty()) {
            playersWaitingAdapter.setData(otherPlayers, game01ViewModel.playersPoints)
            _binding.playersWaiting.smoothScrollToPosition(0)
        }
    }

    private fun goToPlayersStatsScreen(startingPoints: Int) {
        val x01PlayerList = emptyList<X01PlayerStats>().toMutableList()
        game01ViewModel.players.forEach {
            x01PlayerList.add(
                X01PlayerStats(
                    it,
                    currentScore = startingPoints.minus(
                        DartsUtils.getPlayerScore(args.isBull25, it, game01ViewModel.playersPoints, args.inIndex)),
                    checkout = DartsUtils.getScoreFromPointList(
                        DartsUtils.getPlayerRoundDarts(it, game01ViewModel.currentRound, game01ViewModel.playersPoints),
                        args.isBull25
                    ),
                    ppd = DartsUtils.getPlayerPPD(it, game01ViewModel.playersPoints, args.isBull25)
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
}