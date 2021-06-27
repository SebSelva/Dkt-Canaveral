package com.decathlon.canaveral.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import com.decathlon.canaveral.R
import timber.log.Timber

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

        val pointsRemainingView = view.findViewById<AppCompatTextView>(R.id.dart_points_remaining)
        val keyboardView = view.findViewById<GridView>(R.id.keyboard_dkt)

        pointsRemainingView.setTextColor(AppCompatResources.getColorStateList(pointsRemainingView.context, R.color.blue_dkt_secondary))

        val keyboardAdapter = KeyboardAdapter(view.context,
            { value, multiple ->
                val result = if (value != 25) {
                    if (multiple != null) "$multiple$value" else "$value"
                } else {
                    if (multiple != null) "$multiple-Bull" else "Bull"
                }

                Timber.d("Point to add : $result")
                pointsRemainingView.text = result },
            {
                Timber.d("Last point cancelled")
                pointsRemainingView.text = "Del !"
            })

        keyboardView.adapter = keyboardAdapter
    }
}
