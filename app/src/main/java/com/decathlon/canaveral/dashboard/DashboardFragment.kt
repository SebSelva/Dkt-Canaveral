package com.decathlon.canaveral.dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.CanaveralApp
import com.decathlon.canaveral.CanaveralViewModelFactory
import com.decathlon.canaveral.R
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel = ViewModelProviders.of(this, CanaveralViewModelFactory).get(DashboardViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {

            val versusMode = resources.getStringArray(R.array.versus_type)
            val versusField = view.findViewById<TextInputLayout>(R.id.input_versus)
            (versusField.editText as? AutoCompleteTextView)?.setAdapter(ArrayAdapter(requireContext(),
                R.layout.list_textview_item, versusMode))
            (versusField.editText as? AutoCompleteTextView)?.setText(versusMode[0],false)

            val gameType = resources.getStringArray(R.array.game_type_array)
            val gameField = view.findViewById<TextInputLayout>(R.id.input_game)
            (gameField.editText as? AutoCompleteTextView)?.setAdapter(ArrayAdapter(requireContext(),
                R.layout.list_textview_item, gameType))
            (gameField.editText as? AutoCompleteTextView)?.setText(gameType[0],false)

            val gameVariant = resources.getStringArray(R.array.zero_game_type_array)
            val variantField = view.findViewById<TextInputLayout>(R.id.input_variant)
            (variantField.editText as? AutoCompleteTextView)?.setAdapter(ArrayAdapter(requireContext(),
                R.layout.list_textview_item, gameVariant))
            (variantField.editText as? AutoCompleteTextView)?.setText(gameVariant[0],false)

            val playerAdapter = PlayerAdapter(resources.getInteger(R.integer.player_max)) {
                dashboardViewModel.addPlayer(it)
            }

            val playerRecycler = view.findViewById<RecyclerView>(R.id.rv_player)

            playerRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            playerRecycler.setHasFixedSize(true)
            playerRecycler.adapter = playerAdapter

            view.findViewById<Button>(R.id.start_btn).setOnClickListener {
                val gameSelected = (gameField.editText as? AutoCompleteTextView)?.text.toString()
                val variantSelected =
                    (variantField.editText as? AutoCompleteTextView)?.text.toString()
                Toast.makeText(context, "$gameSelected : $variantSelected", Toast.LENGTH_SHORT)
                    .show()
            }

            dashboardViewModel.playerLiveData.observe(viewLifecycleOwner, Observer {
                Timber.d("Dashboard see %d players", it.size)
                (versusField.editText as? AutoCompleteTextView)?.setText(versusMode[it.size-1],false)
            })
            dashboardViewModel.getPlayers()
        }

    }
}