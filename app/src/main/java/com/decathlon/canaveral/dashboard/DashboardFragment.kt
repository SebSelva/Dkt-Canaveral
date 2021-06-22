package com.decathlon.canaveral.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.R
import com.google.android.material.textfield.TextInputLayout
import org.koin.android.ext.android.get
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel = get()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {

            // Versus Mode selection
            val versusMode = resources.getStringArray(R.array.versus_type)
            val versusField = view.findViewById<AutoCompleteTextView>(R.id.input_versus)
            versusField?.setAdapter(ArrayAdapter(requireContext(),
                R.layout.list_textview_item, versusMode))
            versusField?.setText(versusMode[0],false)
            versusField?.dropDownVerticalOffset = 4
            versusField?.setOnTouchListener { _, _ ->
                versusField.showDropDown()
                true
            }

            // Game Type selection
            val gameType = resources.getStringArray(R.array.game_type_array)
            val gameField = view.findViewById<AutoCompleteTextView>(R.id.input_game)
            gameField?.setAdapter(ArrayAdapter(requireContext(),
                R.layout.list_textview_item, gameType))
            gameField?.setText(gameType[0],false)
            gameField?.dropDownVerticalOffset = 4
            gameField?.setOnTouchListener { _, _ ->
                gameField.showDropDown()
                true
            }

            // Game Variant selection
            val gameVariant = resources.getStringArray(R.array.zero_game_type_array)
            val variantField = view.findViewById<AutoCompleteTextView>(R.id.input_variant)
            variantField?.setAdapter(ArrayAdapter(requireContext(),
                R.layout.list_textview_item, gameVariant))
            variantField?.setText(gameVariant[0],false)
            variantField?.dropDownVerticalOffset = 4
            variantField?.setOnTouchListener { _, _ ->
                variantField.showDropDown()
                true
            }

            val playerAdapter = PlayerAdapter(resources.getInteger(R.integer.player_max),
                {dashboardViewModel.addPlayer(it)}, {dashboardViewModel.removePlayer(it)})

            val playerRecycler = view.findViewById<RecyclerView>(R.id.rv_player)

            playerRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            playerRecycler.adapter = playerAdapter

            view.findViewById<Button>(R.id.start_btn).setOnClickListener {
                val gameSelected = gameField?.text.toString()
                val variantSelected = variantField?.text.toString()
                Toast.makeText(context, "$gameSelected : $variantSelected", Toast.LENGTH_SHORT)
                    .show()
            }

            dashboardViewModel.playerLiveData.observe(viewLifecycleOwner, Observer {
                Timber.d("Dashboard see %d players", it.size)
                playerAdapter.setData(it)
                versusField?.setText(versusMode[if (it.isEmpty()) 0 else (it.size-1)],false)
            })
            dashboardViewModel.getPlayers()
        }

    }
}