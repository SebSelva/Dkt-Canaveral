package com.decathlon.canaveral.dashboard

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.databinding.FragmentDashboardBinding
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_DETAIL_IN
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_DETAIL_IS_BULL_25
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_DETAIL_OUT
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_GAME_VARIANT
import com.decathlon.canaveral.game.GameActivity.Companion.BUNDLE_KEY_PLAYERS
import com.decathlon.canaveral.game.GameActivityArgs
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel = get()
    private lateinit var _binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        _binding = FragmentDashboardBinding.bind(view)
        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {

            // Versus Mode selection
            val versusMode = resources.getStringArray(R.array.versus_type)
            initSpinner(versusMode, _binding.inputVersus)

            // Game Type selection
            val gameType = resources.getStringArray(R.array.game_type_array)
            initSpinner(gameType, _binding.inputGame)
            _binding.inputGame.isEnabled = gameType.size > 1

            // Game Variant selection
            val gameVariant = resources.getStringArray(R.array.zero_game_type_array)
            initSpinner(gameVariant, _binding.inputVariant)

            // Players
            val playerAdapter = PlayerAdapter(resources.getInteger(R.integer.player_max),
                {dashboardViewModel.addPlayer(it)}, {dashboardViewModel.removePlayer(it)})
            _binding.playersRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            _binding.playersRecyclerView.adapter = playerAdapter

            // Game details - In
            val gameDetailsInOut = resources.getStringArray(R.array.game_detail_inout_values)
            initSpinner(gameDetailsInOut, _binding.inputGameDetailIn)

            // Game details - Out
            initSpinner(gameDetailsInOut, _binding.inputGameDetailOut)

            // Game details - Bull
            val detailBullValues = resources.getStringArray(R.array.game_detail_bull_values)
            initSpinner(detailBullValues, _binding.inputGameDetailBull)

            // Game details layout animation
            addGameDetailsAnimation(view)

            // Start button
            _binding.startBtn.setOnClickListener {

                Navigation.findNavController(view).navigate(
                    R.id.action_dashboard_to_game,
                    GameActivityArgs(
                        gameVariant.indexOf(_binding.inputVariant.text.toString()),
                        detailBullValues.indexOf(_binding.inputGameDetailBull.text.toString()) == 0,
                        20,
                        gameDetailsInOut.indexOf(_binding.inputGameDetailIn.text.toString()),
                        gameDetailsInOut.indexOf(_binding.inputGameDetailOut.text.toString())
                    ).toBundle())
            }

            // Data launch
            dashboardViewModel.playerLiveData.observe(viewLifecycleOwner, {
                playerAdapter.setData(it)
                val versusIndex = if (it.isEmpty()) 0 else (it.size-1)
                _binding.inputVersus.setText(versusMode[versusIndex],false)
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
