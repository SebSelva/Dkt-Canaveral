package com.decathlon.canaveral.dashboard

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.R
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_DETAIL_IN
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_DETAIL_IS_BULL_25
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_DETAIL_OUT
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_SELECTED
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_VARIANT
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_PLAYERS
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
            initSpinner(versusMode, versusField)

            // Game Type selection
            val gameType = resources.getStringArray(R.array.game_type_array)
            val gameField = view.findViewById<AutoCompleteTextView>(R.id.input_game)
            initSpinner(gameType, gameField)
            gameField?.isEnabled = gameType.size > 1

            // Game Variant selection
            val gameVariant = resources.getStringArray(R.array.zero_game_type_array)
            val variantField = view.findViewById<AutoCompleteTextView>(R.id.input_variant)
            initSpinner(gameVariant, variantField)

            // Players
            val playerAdapter = PlayerAdapter(resources.getInteger(R.integer.player_max),
                {dashboardViewModel.addPlayer(it)}, {dashboardViewModel.removePlayer(it)})
            val playerRecycler = view.findViewById<RecyclerView>(R.id.rv_player)
            playerRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            playerRecycler.adapter = playerAdapter

            // Game details - In
            val gameDetailsInOut = resources.getStringArray(R.array.game_detail_inout_values)
            val detailInField = view.findViewById<AutoCompleteTextView>(R.id.input_game_detail_in)
            initSpinner(gameDetailsInOut, detailInField)

            // Game details - Out
            val detailOutField = view.findViewById<AutoCompleteTextView>(R.id.input_game_detail_out)
            initSpinner(gameDetailsInOut, detailOutField)

            // Game details - Bull
            val detailBullValues = resources.getStringArray(R.array.game_detail_bull_values)
            val detailBullField = view.findViewById<AutoCompleteTextView>(R.id.input_game_detail_bull)
            initSpinner(detailBullValues, detailBullField)

            // Game details layout animation
            addGameDetailsAnimation(view)

            // Start button
            view.findViewById<Button>(R.id.start_btn).setOnClickListener {
                val bundle = Bundle()

                bundle.putInt(BUNDLE_KEY_GAME_SELECTED, gameType.indexOf(gameField.text.toString()))
                bundle.putInt(BUNDLE_KEY_GAME_VARIANT, gameVariant.indexOf(variantField.text.toString()))
                bundle.putInt(BUNDLE_KEY_GAME_DETAIL_IN, gameDetailsInOut.indexOf(detailInField.text.toString()))
                bundle.putInt(BUNDLE_KEY_GAME_DETAIL_OUT, gameDetailsInOut.indexOf(detailOutField.text.toString()))
                bundle.putBoolean(BUNDLE_KEY_GAME_DETAIL_IS_BULL_25, detailBullValues.indexOf(detailBullField.text.toString()) == 0)
                bundle.putParcelableArrayList(BUNDLE_KEY_PLAYERS, ArrayList(dashboardViewModel.playerLiveData.value))

                Navigation.findNavController(view).navigate(R.id.action_dashboard_to_game, bundle)
            }

            // Data launch
            dashboardViewModel.playerLiveData.observe(viewLifecycleOwner, {
                playerAdapter.setData(it)
                val versusIndex = if (it.isEmpty()) 0 else (it.size-1)
                versusField?.setText(versusMode[versusIndex],false)
            })
            dashboardViewModel.getPlayers()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSpinner(array: Array<String>, field :AutoCompleteTextView?) {
        field?.setAdapter(ArrayAdapter(requireContext(),
            R.layout.list_textview_item, array))
        field?.setText(array[0],false)
        field?.dropDownVerticalOffset = resources.getInteger(R.integer.dropdown_menu_vertical_offset)
        field?.setOnTouchListener { _, _ ->
            field.showDropDown()
            true
        }
    }

    private fun addGameDetailsAnimation(view : View) {
        val detailTitle = view.findViewById<LinearLayoutCompat>(R.id.game_details_title)
        val detailLeftIcon = view.findViewById<AppCompatImageView>(R.id.game_details_title_left)
        val detailRightIcon = view.findViewById<AppCompatImageView>(R.id.game_details_title_right)
        val detailLayout = view.findViewById<LinearLayoutCompat>(R.id.game_details_layout)
        val animationDuration = resources.getInteger(R.integer.animation_duration).toLong()

        // Init layout state
        detailLayout.animate().apply {
            duration = animationDuration
            translationY(-detailLayout.height.toFloat())
            alpha(0F)
            detailLayout.isVisible = false
        }

        // Layout animation
        detailTitle.setOnClickListener {
            val isOpened = detailLayout.isVisible
            detailLeftIcon.rotation = if (isOpened) 0F else 90F
            detailRightIcon.rotation = if (isOpened) 180F else 90F
            if (isOpened) {
                detailLayout.animate().apply {
                    duration = animationDuration
                    translationY(-detailLayout.height.toFloat())
                    alpha(0F)
                    setListener(object :AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            detailLayout.isVisible = false
                        }
                    })
                }
            } else {
                detailLayout.isVisible = true
                detailLayout.animate().apply {
                    duration = animationDuration
                    translationY(0F)
                    alpha(1F)
                    setListener(null)
                }
            }
        }
    }
}
