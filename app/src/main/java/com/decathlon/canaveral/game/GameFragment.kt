package com.decathlon.canaveral.game

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.DartsUtils
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_DETAIL_IS_BULL_25
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_VARIANT
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_PLAYERS
import com.decathlon.core.player.model.Player
import kotlinx.coroutines.*
import org.koin.android.ext.android.get
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class GameFragment : Fragment() {

    private val game01ViewModel: Game01ViewModel = get()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
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
        // Player name
        val playerName = view.findViewById<AppCompatTextView>(R.id.player_name)

        // Player points remaining
        val pointsRemainingView = view.findViewById<AppCompatTextView>(R.id.player_points_remaining)
        pointsRemainingView.typeface =
            Typeface.createFromAsset(view.context.assets, "klavika-bold-italic.otf")
        pointsRemainingView.text = startingPoints.toString()

        // Player darts points
        val playerDartsPointsRecycler = view.findViewById<RecyclerView>(R.id.player_darts_points)
        playerDartsPointsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val playerPointsAdapter = PlayerPointsAdapter()
        playerDartsPointsRecycler.adapter = playerPointsAdapter
        playerPointsAdapter.setData(emptyList(), false)

        // Players waiting
        val playersWaitingRecycler = view.findViewById<RecyclerView>(R.id.players_waiting)
        playersWaitingRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val playersWaitingAdapter = PlayersWaitingAdapter(startingPoints, isBull25 == true)
        playersWaitingRecycler.adapter = playersWaitingAdapter

        // Keyboard
        val keyboardView = view.findViewById<GridView>(R.id.keyboard_dkt)
        val keyboardAdapter = KeyboardAdapter(
            view.context,
            { point -> game01ViewModel.addPlayerPoint(point) },
            { game01ViewModel.removeLastPlayerPoint() })
        keyboardView.adapter = keyboardAdapter

        // ViewModel observers
        game01ViewModel.currentPlayerLiveData.observe(viewLifecycleOwner, {
            playerName.text = it?.nickname
            pointsRemainingView.text =
                startingPoints.minus(
                    DartsUtils.getPlayerScore(
                        isBull25 == true,
                        it,
                        game01ViewModel.playersPointsLivedata.value
                    )
                )
                    .toString()

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
                    game01ViewModel.currentPlayerLiveData.value!!,
                    it
                ), !game01ViewModel.isStackIncreasing
            )
            startScoreAnimation(
                pointsRemainingView,
                (pointsRemainingView.text as String).toInt(),
                startingPoints.minus(
                    DartsUtils.getPlayerScore(
                        isBull25 == true,
                        game01ViewModel.currentPlayerLiveData.value!!,
                        it
                    )
                )
            )

            // Go to next player
            if (DartsUtils.isPlayerRoundComplete(it)) {
                job = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    delay(if (game01ViewModel.isStackIncreasing) 2200 else 3200)
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
}
