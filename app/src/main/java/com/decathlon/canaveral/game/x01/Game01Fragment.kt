package com.decathlon.canaveral.game.x01

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.model.PlayerStats
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.canaveral.databinding.FragmentGameBinding
import com.decathlon.canaveral.game.GameStatsFragmentArgs
import com.decathlon.canaveral.game.GameStatsViewModel
import com.decathlon.canaveral.game.adapter.KeyboardAdapter
import com.decathlon.canaveral.game.adapter.PlayerPointsAdapter
import com.decathlon.canaveral.game.adapter.PlayersWaitingAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

open class Game01Fragment : BaseFragment<FragmentGameBinding>() {

    private val args: Game01FragmentArgs by navArgs()
    private val game01ViewModel by viewModel<Game01ViewModel>()
    private val gameStatsViewModel by sharedViewModel<GameStatsViewModel>()

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
            lifecycleScope.launchWhenResumed {
                if (jobNextPlayer?.isActive != true) {
                    findNavController().navigate(R.id.action_01game_to_options)
                }
            }
        }
        val isPortraitMode = resources.configuration.orientation == ORIENTATION_PORTRAIT

        // Player points remaining
        _binding.playerPointsRemaining.text = game01ViewModel.startingPoints.toString()

        // Player darts points
        playerPointsAdapter = PlayerPointsAdapter()
        _binding.playerDartsPoints.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false)
            adapter = playerPointsAdapter
        }
        playerPointsAdapter.setData(emptyList(), false)

        // Players waiting
        playersWaitingAdapter = PlayersWaitingAdapter(game01ViewModel.startingPoints, game01ViewModel.isBull25, game01ViewModel.inValue)
        _binding.playersWaiting.apply {
            layoutManager = if (isPortraitMode) {
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            } else {
                val dividerItemDecoration = DividerItemDecoration(context, GridLayoutManager.HORIZONTAL)
                AppCompatResources.getDrawable(context, R.drawable.vertical_divider)
                    ?.let { dividerItemDecoration.setDrawable(it) }
                addItemDecoration(dividerItemDecoration)
                GridLayoutManager(requireContext(), 3, GridLayoutManager.HORIZONTAL, false)
            }
            adapter = playersWaitingAdapter
        }

        // Keyboard
        val keyboardAdapter = KeyboardAdapter(
            view.context,
            getKeyboardType(),
            { point -> game01ViewModel.addPlayerPoint(point) },
            { game01ViewModel.removeLastPlayerPoint() })
        val layoutManager = GridLayoutManager(
            context,
            if (isPortraitMode) { 10 } else { 9 },
            GridLayoutManager.VERTICAL,
            false)
        layoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                if (isPortraitMode && position >= 20) 2 else 1
        }
        _binding.keyboardDkt.layoutManager = layoutManager
        _binding.keyboardDkt.adapter = keyboardAdapter

        // ViewModel observers
        game01ViewModel.currentPlayerLiveData.observe(viewLifecycleOwner) {
            onUpdateCurrentPlayer(it, game01ViewModel.nbRounds, game01ViewModel.startingPoints)
        }
        game01ViewModel.getCurrentPlayer()

        game01ViewModel.playersPointsLivedata.observe(viewLifecycleOwner) {
            onUpdatePlayersPoints(it, game01ViewModel.startingPoints, game01ViewModel.nbRounds)
        }
        game01ViewModel.getPlayersPoints()
    }

    private fun onUpdatePlayersPoints(
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
            game01ViewModel.isPointBlinking
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

        // Other players ordered
        game01ViewModel.currentPlayerLiveData.value?.let {
            val otherPlayers = getWaitingPlayersOrdered(
                it, game01ViewModel.players)
            if (otherPlayers.isNotEmpty()) {
                playersWaitingAdapter.setData(otherPlayers, game01ViewModel.playersPoints)
                _binding.playersWaiting.smoothScrollToPosition(0)
            }
        }

        // Test if game is finished
        if (DartsUtils.is01GameFinished(startingPoints, game01ViewModel.isBull25, nbRounds, game01ViewModel.inValue, game01ViewModel.players, stack)) {
            // Generate STD ACTIVITY here to be sent
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
                        showTransitionInfo(R.id.action_01game_to_score,
                            resources.getString(R.string.game_round_bust))
                        // Wait to score animation finish
                        delay(400)
                    }
                    game01ViewModel.isStackIncreasing -> {
                        delay(2000)
                        // SHOW TRICK DONE
                        game01ViewModel.updateActivity(gameStatsViewModel.winPlayers)
                        showTransitionInfo(R.id.action_01game_to_score,
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
        startingPoints: Int
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
        _binding.invalidateAll()
    }

    private fun goToPlayersStatsScreen(startingPoints: Int) {
        val x01PlayerList = emptyList<PlayerStats>().toMutableList()
        game01ViewModel.players.forEach {
            x01PlayerList.add(
                PlayerStats(
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
            // Time to show transition then back to this screen
            // Before transition max = 2000
            // Added to transition = 2000
            delay(4200)
            findNavController().navigate(R.id.action_01game_to_end,
                GameStatsFragmentArgs(
                    x01PlayerList.toTypedArray(),
                    args.gameTypeIndex,
                    args.variantIndex,
                    args.isBull25,
                    args.roundIndex,
                    args.inIndex,
                    args.outIndex
                ).toBundle())
        }
    }
}
