package com.decathlon.canaveral.stats

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseDialogFragment
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.canaveral.databinding.DialogTrickInfoBinding

class TrickInfoDialog: BaseDialogFragment<DialogTrickInfoBinding>() {
    override var layoutId = R.layout.dialog_trick_info
    private val args: TrickInfoDialogArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        _binding.trickInfoClose.setOnClickListener {
            dismiss()
        }

        _binding.trickName.text = requireContext().resources.getString(getTrickTitle(args.statTitle))
        _binding.trickDescription.text = requireContext().resources.getString(DartsUtils.getStatDescription(args.statTitle))
        _binding.trickValue.text = args.statValue.toString()

        val levelSteps = requireContext().resources.getIntArray(DartsUtils.getArrayLevel(args.statTitle))
        _binding.trickLevel1Value.text = levelSteps[0].toString()
        _binding.trickLevel2Value.text = levelSteps[1].toString()
        _binding.trickLevel3Value.text = levelSteps[2].toString()
        _binding.trickLevel4Value.text = levelSteps[3].toString()
        var lastIndex = 0
        for (i in levelSteps.indices) {
            if (args.statValue >= levelSteps[i]) lastIndex = i+1
        }
        val drawable = when (lastIndex) {
            1 -> R.drawable.ic_stat_level_1
            2 -> R.drawable.ic_stat_level_2
            3 -> R.drawable.ic_stat_level_3
            4 -> R.drawable.ic_stat_level_4
            else -> R.drawable.ic_stat_level_0
        }
        Glide.with(_binding.trickLevelCircle.context).load(drawable).into(_binding.trickLevelCircle)

        _binding.trickNextStep.text = "/ "+levelSteps[lastIndex]
        _binding.trickLevel1.alpha = if (lastIndex < 1) 0.25F else 1F
        _binding.trickLevel2.alpha = if (lastIndex < 2) 0.25F else 1F
        _binding.trickLevel3.alpha = if (lastIndex < 3) 0.25F else 1F
        _binding.trickLevel4.alpha = if (lastIndex < 4) 0.25F else 1F

        _binding.trickLevelNextDescription.text = getTrickNextLevelDescription(args.statTitle, lastIndex, levelSteps)
    }

    private fun getTrickNextLevelDescription(statTitle: Int, currentIndex: Int, levelSteps: IntArray): String? {
        return if (currentIndex >= 4) {
            requireContext().resources.getString(R.string.stats_trick_step_max)
        } else {
            when (statTitle) {
                R.string.stats_trick_baby_ton,
                R.string.stats_trick_bag_o_nuts,
                R.string.stats_trick_bulls_eye,
                R.string.stats_trick_bust,
                R.string.stats_trick_hattrick,
                R.string.stats_trick_high_ton,
                R.string.stats_trick_low_ton,
                R.string.stats_trick_three_in_a_bed,
                R.string.stats_trick_ton,
                R.string.stats_trick_ton_80,
                R.string.stats_trick_white_horse,
                R.string.stats_trick_round_score_60_and_more,
                R.string.stats_trick_round_score_100_and_more,
                R.string.stats_trick_round_score_140_and_more,
                R.string.stats_trick_round_score_180,
                R.string.stats_trick_field_15,
                R.string.stats_trick_field_16,
                R.string.stats_trick_field_17,
                R.string.stats_trick_field_18,
                R.string.stats_trick_field_19,
                R.string.stats_trick_field_20,
                R.string.stats_trick_field_bull,
                R.string.stats_trick_checkout_rate ->
                    requireContext().resources.getString(R.string.stats_trick_count_next_step,
                        levelSteps[currentIndex],
                        requireContext().resources.getString(getTrickTitle(args.statTitle)))

                R.string.stats_trick_avg_score_dart1,
                R.string.stats_trick_avg_score_dart2,
                R.string.stats_trick_avg_score_dart3,
                R.string.stats_trick_avg_score_round ->
                    requireContext().resources.getString(R.string.stats_trick_average_next_step,
                    levelSteps[currentIndex])

                R.string.stats_trick_highest_score_round,
                R.string.stats_trick_highest_checkout,
                R.string.stats_trick_highest_8rounds,
                R.string.stats_trick_highest_12rounds,
                R.string.stats_trick_highest_16rounds ->
                    requireContext().resources.getString(R.string.stats_trick_score_next_step,
                        levelSteps[currentIndex])
                else -> null
            }
        }
    }

    private fun getTrickTitle(titleRes : Int): Int {
        return when (titleRes) {
            R.string.stats_trick_avg_score_dart1 -> R.string.stats_trick_avg_score_dart1_title
            R.string.stats_trick_avg_score_dart2 -> R.string.stats_trick_avg_score_dart2_title
            R.string.stats_trick_avg_score_dart3 -> R.string.stats_trick_avg_score_dart3_title
            R.string.stats_trick_avg_score_round -> R.string.stats_trick_avg_score_round_title
            R.string.stats_trick_checkout_rate -> R.string.stats_trick_checkout_rate_title
            R.string.stats_trick_highest_checkout -> R.string.stats_trick_highest_checkout_title
            R.string.stats_trick_highest_score_round -> R.string.stats_trick_highest_score_round_title
            R.string.stats_trick_highest_8rounds -> R.string.stats_trick_highest_8rounds_title
            R.string.stats_trick_highest_12rounds -> R.string.stats_trick_highest_12rounds_title
            R.string.stats_trick_highest_16rounds -> R.string.stats_trick_highest_16rounds_title
            else -> titleRes
        }
    }
}