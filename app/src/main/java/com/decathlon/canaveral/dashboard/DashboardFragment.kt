package com.decathlon.canaveral.dashboard

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.databinding.FragmentDashboardBinding
import com.decathlon.canaveral.game.GameActivityArgs
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {

    private val dashboardViewModel by viewModel<DashboardViewModel>()
    override var layoutId = R.layout.fragment_dashboard

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val versusMode = resources.getStringArray(R.array.versus_type)
        val gameType = resources.getStringArray(R.array.game_type_array)

        // Versus Mode selection
        initSpinner(versusMode, _binding.inputVersus)

        // Game Type selection
        _binding.inputGame.isEnabled = gameType.size > 1
        _binding.inputGame.setOnItemClickListener { _, _, position, _ ->
            setFormValues(gameType[position])
            setGameTypeDetailsVisibility(gameType[position])
        }
        initSpinner(gameType, _binding.inputGame)

        // Game Variant selection
        initSpinner(resources.getStringArray(R.array.game_x01_variant_array), _binding.inputVariant)

        // Players
        val playerAdapter = PlayerAdapter(resources.getInteger(R.integer.player_max),
            {dashboardViewModel.addPlayer(it)}, {dashboardViewModel.removePlayer(it)})
        _binding.playersRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        _binding.playersRecyclerView.adapter = playerAdapter

        // Game details - In
        initSpinner(resources.getStringArray(R.array.game_detail_inout_values), _binding.inputGameDetailIn)

        // Game details - Out
        initSpinner(resources.getStringArray(R.array.game_detail_inout_values), _binding.inputGameDetailOut)

        // Game details - Bull
        initSpinner(resources.getStringArray(R.array.game_detail_bull_values), _binding.inputGameDetailBull)

        // Game details - Round
        initSpinner(resources.getStringArray(R.array.game_x01_detail_round), 1, _binding.inputGameDetailRound)

        // Game details layout animation
        addGameDetailsAnimation(view)

        // Start button
        _binding.startBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(
                R.id.action_dashboard_to_game,
                getGameBundle(_binding.inputGame.text.toString()))
        }

        // Data launch
        dashboardViewModel.playerLiveData.observe(viewLifecycleOwner, {
            playerAdapter.setData(it)
            val versusIndex = if (it.isEmpty()) 0 else (it.size-1)
            _binding.inputVersus.setText(versusMode[versusIndex],false)
        })
        dashboardViewModel.getPlayers()
    }

    private fun getGameBundle(gameType: String): Bundle? {
        when(gameType) {
            resources.getString(R.string.game_type_01_game) -> {
                return GameActivityArgs(
                    resources.getStringArray(R.array.game_type_array).indexOf(gameType),
                    resources.getStringArray(R.array.game_x01_variant_array).indexOf(_binding.inputVariant.text.toString()),
                    resources.getStringArray(R.array.game_detail_bull_values).indexOf(_binding.inputGameDetailBull.text.toString()) == 0,
                    resources.getStringArray(R.array.game_x01_detail_round).indexOf(_binding.inputGameDetailRound.text.toString()),
                    resources.getStringArray(R.array.game_detail_inout_values).indexOf(_binding.inputGameDetailIn.text.toString()),
                    resources.getStringArray(R.array.game_detail_inout_values).indexOf(_binding.inputGameDetailOut.text.toString())
                ).toBundle()
            }
            resources.getString(R.string.game_type_count_up) -> {
                return GameActivityArgs(
                    resources.getStringArray(R.array.game_type_array).indexOf(gameType),
                    resources.getStringArray(R.array.game_countup_variant_array).indexOf(_binding.inputVariant.text.toString()),
                    resources.getStringArray(R.array.game_detail_bull_values).indexOf(_binding.inputCountupDetailBull.text.toString()) == 0,
                    resources.getStringArray(R.array.game_countup_detail_round).indexOf(_binding.inputCountupDetailRound.text.toString())
                ).toBundle()
            }
            else -> return null
        }
    }

    private fun setFormValues(gameType: String) {
        when (gameType) {
            resources.getString(R.string.game_type_count_up) -> {
                initSpinner(
                    resources.getStringArray(R.array.game_countup_variant_array),
                    _binding.inputVariant
                )
                initSpinner(
                    resources.getStringArray(R.array.game_countup_detail_round),
                    _binding.inputCountupDetailRound
                )
                initSpinner(
                    resources.getStringArray(R.array.game_detail_bull_values),
                    _binding.inputCountupDetailBull
                )
            }
            else -> {
                initSpinner(
                    resources.getStringArray(R.array.game_x01_variant_array),
                    _binding.inputVariant
                )
                initSpinner(
                    resources.getStringArray(R.array.game_x01_detail_round),
                    1,
                    _binding.inputGameDetailRound
                )
                initSpinner(
                    resources.getStringArray(R.array.game_detail_bull_values),
                    _binding.inputGameDetailBull
                )
            }
        }
    }

    private fun setGameTypeDetailsVisibility(gameType: String) {
        _binding.gameDetails01Game.isVisible =
            gameType == resources.getString(R.string.game_type_01_game)
        _binding.gameDetailsCountup.isVisible =
            gameType == resources.getString(R.string.game_type_count_up)
    }

    private fun initSpinner(array: Array<String>, field :AutoCompleteTextView?) {
        initSpinner(array, 0, field)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSpinner(array: Array<String>, initIndex: Int, field :AutoCompleteTextView?) {
        field?.setAdapter(MyAdapter(requireContext(),
            R.layout.list_textview_item, array))
        field?.setText(array[initIndex],false)
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

    /**
     * Workaround to always have dropdown menu choice list filled
     */
    class MyAdapter(context: Context, layoutId: Int, items: Array<String>)
        : ArrayAdapter<String>(context, layoutId, items) {

        private val noOpFilter = object : Filter() {
            private val noOpResult = FilterResults()
            override fun performFiltering(constraint: CharSequence?) = noOpResult
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Workaround to not show filtered results
            }
        }

        override fun getFilter() = noOpFilter
    }
}
