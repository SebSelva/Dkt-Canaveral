package com.decathlon.canaveral.stats.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.databinding.ItemListGameDetailsStatsBinding
import com.decathlon.canaveral.stats.model.StatItem

class GameStatDetailsAdapter: RecyclerView.Adapter<BaseViewHolder<StatItem>>() {

    private var statItemList = ArrayList<StatItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(itemList: List<StatItem>) {
        statItemList.clear()
        statItemList.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<StatItem> {
        return GameStatDetailItem(
            ItemListGameDetailsStatsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<StatItem>, position: Int) {
        holder.bind(statItemList[position])
    }

    override fun getItemCount(): Int {
        return statItemList.size
    }

    inner class GameStatDetailItem(private var binding: ItemListGameDetailsStatsBinding): BaseViewHolder<StatItem>(binding.root)  {
        override fun bind(item: StatItem) {
            binding.gameStatsDetailTitle.text = binding.root.context.resources.getString(item.title)
            binding.gameStatsDetailValue.text = if (item.value is Float) String.format("%.2f", item.value) else item.value.toString()
            if (bindingAdapterPosition % 2 == 0) binding.root.setBackgroundColor(
                binding.root.context.resources.getColor(R.color.blue_dkt_50, binding.root.context.theme)
            )
        }
    }
}