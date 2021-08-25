package com.decathlon.canaveral.game.countup

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.model.PlayerStats
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.canaveral.game.adapter.KeyboardAdapter
import com.decathlon.canaveral.game.adapter.PlayerPointsAdapter
import com.decathlon.canaveral.game.adapter.PlayersWaitingAdapter
import com.decathlon.canaveral.game.x01.Game01Fragment
import com.decathlon.canaveral.game.GameStatsFragmentArgs
import com.decathlon.canaveral.game.adapter.KeyboardType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class GameCountUpFragment : Game01Fragment() {

    private val args: GameCountUpFragmentArgs by navArgs()
    private val countUpViewModel by viewModel<CountUpViewModel>()
    override var layoutId = R.layout.fragment_game

    private lateinit var playerPointsAdapter: PlayerPointsAdapter
    private lateinit var playersWaitingAdapter: PlayersWaitingAdapter
    private var jobNextPlayer: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        countUpViewModel.variant = args.variantIndex
        countUpViewModel.isBull25 = args.isBull25
        countUpViewModel.nbRounds = resources.getStringArray(R.array.game_countup_detail_round)[args.roundIndex].toInt()

        initViews(view)
    }

    private fun initViews(view: View) {

        // Options
        _binding.gameOptions.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                if (jobNextPlayer?.isActive != true) {
                    findNavController().navigate(R.id.action_countup_to_options)
                }
            }
        }

        // Player points remaining
        _binding.playerPointsRemaining.text = countUpViewModel.startingPoints.toString()

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
        playersWaitingAdapter = PlayersWaitingAdapter(countUpViewModel.isBull25)
        _binding.playersWaiting.adapter = playersWaitingAdapter

        // Keyboard
        val keyboardAdapter = KeyboardAdapter(
            view.context,
            getKeyboardType(),
            { point -> countUpViewModel.addPlayerPoint(point) },
            { countUpViewModel.removeLastPlayerPoint() })
        _binding.keyboardDkt.adapter = keyboardAdapter

        // ViewModel observers
        countUpViewModel.currentPlayerLiveData.observe(viewLifecycleOwner) {
            onUpdateCurrentPlayer(
                it,
                countUpViewModel.nbRounds,
                playersWaitingAdapter
            )
        }
        countUpViewModel.getCurrentPlayer()

        countUpViewModel.playersPointsLivedata.observe(viewLifecycleOwner) {
            onUpdatePlayersPoints(
                playerPointsAdapter,
                it,
                countUpViewModel.nbRounds
            )
        }
        countUpViewModel.getPlayersPoints()
    }

    override fun getKeyboardType(): KeyboardType {
        return when(resources.getStringArray(R.array.game_countup_variant_array)[countUpViewModel.variant]) {
            resources.getString(R.string.game_variant_countup_cricket) -> KeyboardType.CRICKET
            resources.getString(R.string.game_variant_countup_bull) -> KeyboardType.BULL
            else -> KeyboardType.NORMAL
        }
    }

    private fun onUpdatePlayersPoints(
        playerPointsAdapter: PlayerPointsAdapter,
        stack: Stack<PlayerPoint>,
        nbRounds: Int?
    ) {
        playerPointsAdapter.setData(
            DartsUtils.getPlayerRoundDarts(
                countUpViewModel.currentPlayer!!,
                countUpViewModel.currentRound,
                stack
            ),
            countUpViewModel.isRoundDecreasing
        )
        val currentScore = DartsUtils.getCountUpPlayerScore(
                countUpViewModel.isBull25,
                countUpViewModel.currentPlayer!!,
                stack
        )
        _binding.playerPdd.text = String.format(
            Locale.ENGLISH, resources.getString(R.string.player_ppd),
            DartsUtils.getPlayerPPD(countUpViewModel.currentPlayer!!, stack, args.isBull25))

        if ((_binding.playerPointsRemaining.text as String).toInt() != currentScore) {
            startScoreAnimation(
                _binding.playerPointsRemaining,
                (_binding.playerPointsRemaining.text as String).toInt(),
                currentScore
            )
        }

        // Test if game is finished
        if (DartsUtils.isCountUpFinished(nbRounds, countUpViewModel.players, stack)) {
            goToPlayersStatsScreen()
        }

        // Go to next player
        if (DartsUtils.isPlayerRoundComplete(
                countUpViewModel.currentPlayerLiveData.value!!,
                countUpViewModel.currentRound,
                stack
            )
        ) {
            jobNextPlayer = viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                when {
                    countUpViewModel.isStackIncreasing -> {
                        delay(2000)
                        showTransitionInfo(R.id.action_countup_to_score,
                            DartsUtils.getScoreFromPointList(
                                DartsUtils.getPlayerLastValidRoundDarts(
                                    0,
                                    countUpViewModel.currentPlayer!!,
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
                countUpViewModel.selectNextPlayer()
            }
        } else if (!countUpViewModel.isStackIncreasing) {
            jobNextPlayer?.cancel()
        }
    }

    private fun onUpdateCurrentPlayer(
        player: Player,
        nbRounds: Int?,
        playersWaitingAdapter: PlayersWaitingAdapter
    ) {
        _binding.playersWaiting.isVisible = (countUpViewModel.players.size > 1)
        _binding.playersWaitingSeparator.isVisible = (countUpViewModel.players.size > 1)

        _binding.currentPlayer = player
        _binding.playerRound.text = if (nbRounds == null) {
            resources.getString(R.string.player_round_unlimited, countUpViewModel.currentRound)
        } else {
            resources.getString(R.string.player_round, countUpViewModel.currentRound, nbRounds)
        }
        _binding.playerPdd.text = String.format(
            Locale.ENGLISH, resources.getString(R.string.player_ppd),
            DartsUtils.getPlayerPPD(player, countUpViewModel.playersPoints, args.isBull25))

        _binding.playerPointsRemaining.text =
            DartsUtils.getCountUpPlayerScore(args.isBull25, player, countUpViewModel.playersPoints)
            .toString()

        // Other players ordered
        val otherPlayers = getWaitingPlayersOrdered(player, countUpViewModel.players)
        if (otherPlayers.isNotEmpty()) {
            playersWaitingAdapter.setData(otherPlayers, countUpViewModel.playersPoints)
            _binding.playersWaiting.smoothScrollToPosition(0)
        }
    }

    private fun goToPlayersStatsScreen() {
        val x01PlayerList = emptyList<PlayerStats>().toMutableList()
        countUpViewModel.players.forEach {
            x01PlayerList.add(
                PlayerStats(
                    it,
                    currentScore = DartsUtils.getCountUpPlayerScore(
                        countUpViewModel.isBull25,
                        it,
                        countUpViewModel.playersPoints
                    ),
                    checkout = DartsUtils.getScoreFromPointList(
                        DartsUtils.getPlayerRoundDarts(it, countUpViewModel.currentRound, countUpViewModel.playersPoints),
                        args.isBull25
                    ),
                    ppd = DartsUtils.getPlayerPPD(it, countUpViewModel.playersPoints, args.isBull25)
                )
            )
        }
        lifecycleScope.launchWhenResumed {
            delay(4200)
            findNavController().navigate(R.id.action_countup_to_end,
                GameStatsFragmentArgs(
                    x01PlayerList.toTypedArray(),
                    args.gameTypeIndex,
                    args.variantIndex,
                    args.isBull25,
                    args.roundIndex
                ).toBundle())
        }
    }
}
