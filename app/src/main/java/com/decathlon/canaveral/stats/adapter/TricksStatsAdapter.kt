package com.decathlon.canaveral.stats.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.canaveral.databinding.*
import com.decathlon.canaveral.stats.model.StatItem

class TricksStatsAdapter(val context: Context,
                         val onTrickClickListener : (StatItem) -> Unit
) : ListAdapter<StatItem, BaseViewHolder<StatItem>>(DiffCallback()) {

    private val tricksList = ArrayList<StatItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: List<StatItem>) {
        tricksList.clear()
        tricksList.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val levelSteps = context.resources.getIntArray(DartsUtils.getArrayLevel(tricksList[position].title))
        var lastIndex = 0
        for (i in levelSteps.indices) {
            val value = DartsUtils.getIntValue(tricksList[position].value)
            if (value >= levelSteps[i]) lastIndex = i+1
        }
        return lastIndex
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
            val arrayLevel = context.resources.getIntArray(DartsUtils.getArrayLevel(item.title))
            val trackLength = arrayLevel[0]
            val trackPosition = DartsUtils.getIntValue(item.value)
            binding.trickProgressIndicator.progress = (trackPosition * 100 / trackLength)

            binding.root.setOnClickListener { onTrickClickListener.invoke(item) }
        }
    }

    inner class TrickLevel1Item(private val binding: ItemListTrickLevel1Binding):
        BaseViewHolder<StatItem>(binding.root) {
        override fun bind(item: StatItem) {
            binding.trickName.text = context.resources.getString(item.title)
            binding.trickScore.text = item.value.toString()
            val arrayLevel = context.resources.getIntArray(DartsUtils.getArrayLevel(item.title))
            val trackLength = arrayLevel[1] - arrayLevel[0]
            val trackPosition = DartsUtils.getIntValue(item.value) - arrayLevel[0]
            binding.trickProgressIndicator.progress = (trackPosition * 100 / trackLength)

            binding.root.setOnClickListener { onTrickClickListener.invoke(item) }
        }
    }

    inner class TrickLevel2Item(private val binding: ItemListTrickLevel2Binding):
        BaseViewHolder<StatItem>(binding.root) {
        override fun bind(item: StatItem) {
            binding.trickName.text = context.resources.getString(item.title)
            binding.trickScore.text = item.value.toString()
            val arrayLevel = context.resources.getIntArray(DartsUtils.getArrayLevel(item.title))
            val trackLength = arrayLevel[2] - arrayLevel[1]
            val trackPosition = DartsUtils.getIntValue(item.value) - arrayLevel[1]
            binding.trickProgressIndicator.progress = (trackPosition * 100 / trackLength)

            binding.root.setOnClickListener { onTrickClickListener.invoke(item) }
        }
    }

    inner class TrickLevel3Item(private val binding: ItemListTrickLevel3Binding):
        BaseViewHolder<StatItem>(binding.root) {
        override fun bind(item: StatItem) {
            binding.trickName.text = context.resources.getString(item.title)
            binding.trickScore.text = item.value.toString()
            val arrayLevel = context.resources.getIntArray(DartsUtils.getArrayLevel(item.title))
            val trackLength = arrayLevel[3] - arrayLevel[2]
            val trackPosition = DartsUtils.getIntValue(item.value) - arrayLevel[2]
            binding.trickProgressIndicator.progress = (trackPosition * 100 / trackLength)

            binding.root.setOnClickListener { onTrickClickListener.invoke(item) }
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

            binding.root.setOnClickListener { onTrickClickListener.invoke(item) }
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