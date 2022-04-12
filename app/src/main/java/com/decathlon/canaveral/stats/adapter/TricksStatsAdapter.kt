package com.decathlon.canaveral.stats.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.databinding.*
import com.decathlon.canaveral.stats.model.StatItem
import kotlin.math.roundToInt

class TricksStatsAdapter(val context: Context): ListAdapter<StatItem, BaseViewHolder<StatItem>>(DiffCallback()) {

    private val tricksList = ArrayList<StatItem>()

    fun setData(items: List<StatItem>) {
        tricksList.clear()
        tricksList.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val levelSteps = context.resources.getIntArray(getArrayLevel(tricksList[position].title))
        var lastIndex = 0
        for (i in levelSteps.indices) {
            val value = getIntValue(tricksList[position].value)
            if (value >= levelSteps[i]) lastIndex = i+1
        }
        return lastIndex
    }

    private fun getIntValue(value: Any): Int {
        return when (value) {
            is Long -> value.toInt()
            is Float -> value.roundToInt()
            else -> 0
        }
    }

    private fun getArrayLevel(levelResInt: Int): Int {
        return when (levelResInt) {
            R.string.stats_trick_baby_ton,
            R.string.stats_trick_bag_o_nuts,
            R.string.stats_trick_bulls_eye,
            R.string.stats_trick_bust,
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
            R.string.stats_trick_field_bull -> R.array.trick_levels

            R.string.stats_trick_highest_score_round -> R.array.high_round_levels

            R.string.stats_trick_avg_score_dart1,
            R.string.stats_trick_avg_score_dart2,
            R.string.stats_trick_avg_score_dart3 -> R.array.average_dart_levels

            R.string.stats_trick_avg_score_round -> R.array.average_round_score_levels

            R.string.stats_trick_checkout_rate -> R.array.rate_levels

            R.string.stats_trick_highest_checkout -> R.array.highest_checkout_levels

            R.string.stats_trick_highest_8rounds -> R.array.highest_score_8rounds_levels
            R.string.stats_trick_highest_12rounds -> R.array.highest_score_12rounds_levels
            R.string.stats_trick_highest_16rounds -> R.array.highest_score_16rounds_levels

            else -> R.array.trick_levels
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<StatItem> {
        return when (TrickLevelViewType.values()[viewType]) {
            TrickLevelViewType.Level0 -> TrickLevel0Item(ItemListTrickLevel0Binding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ))
            TrickLevelViewType.Level1 -> TrickLevel1Item(ItemListTrickLevel1Binding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ))
            TrickLevelViewType.Level2 -> TrickLevel2Item(ItemListTrickLevel2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ))
            TrickLevelViewType.Level3 -> TrickLevel3Item(ItemListTrickLevel3Binding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ))
            TrickLevelViewType.Level4 -> TrickLevel4Item(ItemListTrickLevel4Binding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ))

        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<StatItem>, position: Int) {
        holder.bind(tricksList[position])
    }

    override fun getItemCount(): Int {
        return tricksList.size
    }

    inner class TrickLevel0Item(private val binding: ItemListTrickLevel0Binding):
        BaseViewHolder<StatItem>(binding.root) {
        override fun bind(item: StatItem) {
            binding.trickName.text = context.resources.getString(item.title)
            binding.trickScore.text = item.value.toString()
            val arrayLevel = context.resources.getIntArray(getArrayLevel(item.title))
            val trackLength = arrayLevel[0]
            val trackPosition = getIntValue(item.value)
            binding.trickProgressIndicator.progress = (trackPosition * 100 / trackLength)
        }
    }

    inner class TrickLevel1Item(private val binding: ItemListTrickLevel1Binding):
        BaseViewHolder<StatItem>(binding.root) {
        override fun bind(item: StatItem) {
            binding.trickName.text = context.resources.getString(item.title)
            binding.trickScore.text = item.value.toString()
            val arrayLevel = context.resources.getIntArray(getArrayLevel(item.title))
            val trackLength = arrayLevel[1] - arrayLevel[0]
            val trackPosition = getIntValue(item.value) - arrayLevel[0]
            binding.trickProgressIndicator.progress = (trackPosition * 100 / trackLength)
        }
    }

    inner class TrickLevel2Item(private val binding: ItemListTrickLevel2Binding):
        BaseViewHolder<StatItem>(binding.root) {
        override fun bind(item: StatItem) {
            binding.trickName.text = context.resources.getString(item.title)
            binding.trickScore.text = item.value.toString()
            val arrayLevel = context.resources.getIntArray(getArrayLevel(item.title))
            val trackLength = arrayLevel[2] - arrayLevel[1]
            val trackPosition = getIntValue(item.value) - arrayLevel[1]
            binding.trickProgressIndicator.progress = (trackPosition * 100 / trackLength)
        }
    }

    inner class TrickLevel3Item(private val binding: ItemListTrickLevel3Binding):
        BaseViewHolder<StatItem>(binding.root) {
        override fun bind(item: StatItem) {
            binding.trickName.text = context.resources.getString(item.title)
            binding.trickScore.text = item.value.toString()
            val arrayLevel = context.resources.getIntArray(getArrayLevel(item.title))
            val trackLength = arrayLevel[3] - arrayLevel[2]
            val trackPosition = getIntValue(item.value) - arrayLevel[2]
            binding.trickProgressIndicator.progress = (trackPosition * 100 / trackLength)
        }
    }

    inner class TrickLevel4Item(private val binding: ItemListTrickLevel4Binding):
        BaseViewHolder<StatItem>(binding.root) {
        override fun bind(item: StatItem) {
            binding.trickName.text = context.resources.getString(item.title)
            binding.trickScore.text = item.value.toString()
            val trackLength = 1
            val trackPosition = 1
            binding.trickProgressIndicator.progress = (trackPosition * 100 / trackLength)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<StatItem>() {
        override fun areItemsTheSame(oldItem: StatItem, newItem: StatItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: StatItem, newItem: StatItem): Boolean {
            return oldItem == newItem
        }
    }

    enum class TrickLevelViewType {
        Level0,
        Level1,
        Level2,
        Level3,
        Level4,
    }
}