package com.decathlon.canaveral.game

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.DartsUtils
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_DETAIL_IS_BULL_25
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_VARIANT
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_PLAYERS
import com.decathlon.core.player.model.Player
import org.koin.android.ext.android.get

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

        if (players != null && gameVariant != null) {
            game01ViewModel.setPlayers(players)
            game01ViewModel.variant = resources.getStringArray(R.array.zero_game_type_array)[gameVariant].toInt()
        } else {
            activity?.finish()
        }

        val startingPoints = resources.getStringArray(R.array.zero_game_type_array)[gameVariant!!].toInt()

        // Player name
        val playerName = view.findViewById<AppCompatTextView>(R.id.player_name)

        // Player points remaining
        val pointsRemainingView = view.findViewById<AppCompatTextView>(R.id.player_points_remaining)
        pointsRemainingView.typeface = Typeface.createFromAsset(view.context.assets, "klavika-bold-italic.otf")
        pointsRemainingView.text = game01ViewModel.variant.toString()

        // Player darts points
        val playerDartsPointsRecycler = view.findViewById<RecyclerView>(R.id.player_darts_points)
        playerDartsPointsRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val playerPointsAdapter = PlayerPointsAdapter()
        playerDartsPointsRecycler.adapter = playerPointsAdapter
        playerPointsAdapter.setData(emptyList())

        // Players waiting
        // val playersWaiting = view.findViewById<RecyclerView>(R.id.players_waiting)


        // Keyboard
        val keyboardView = view.findViewById<GridView>(R.id.keyboard_dkt)
        val keyboardAdapter = KeyboardAdapter(view.context,
            { point ->
                playerPointsAdapter.addData(point)
                game01ViewModel.addPlayerPoint(point)
            }, {
                playerPointsAdapter.removeLast()
                game01ViewModel.removeLastPlayerPoint()
            })
        keyboardView.adapter = keyboardAdapter

        // ViewModel observers
        game01ViewModel.currentPlayerLiveData.observe(viewLifecycleOwner, {
            playerName.text = it?.nickname
            pointsRemainingView.text =
                startingPoints.minus(DartsUtils.getPlayerScore(isBull25 == true, it, game01ViewModel.playersPointsLivedata.value))
                    .toString()
        })
        game01ViewModel.getCurrentPlayer()
        game01ViewModel.playersPointsLivedata.observe(viewLifecycleOwner, {
            pointsRemainingView.text = (startingPoints.minus(DartsUtils.getPlayerScore(isBull25 == true, game01ViewModel.currentPlayerLiveData.value!!, it)).toString())
            if (DartsUtils.isPlayerRoundComplete(it)) {
                playerPointsAdapter.setData(emptyList())
                game01ViewModel.selectNextPlayer()
            } else {
                playerPointsAdapter.setData(DartsUtils.getPlayerLastPoints(it))
            }
        })
        game01ViewModel.getPlayersPoints()
    }
}
