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

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class GameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Player points remaining
        val pointsRemainingView = view.findViewById<AppCompatTextView>(R.id.player_points_remaining)
        pointsRemainingView.typeface = Typeface.createFromAsset(view.context.assets, "klavika-bold-italic.otf")
        pointsRemainingView.text = "301"

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
            { point -> playerPointsAdapter.addData(point) },
            { playerPointsAdapter.removeLast() })
        keyboardView.adapter = keyboardAdapter
    }
}
