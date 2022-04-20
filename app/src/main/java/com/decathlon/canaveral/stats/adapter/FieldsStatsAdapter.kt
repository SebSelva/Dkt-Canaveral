package com.decathlon.canaveral.stats.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.databinding.ItemListFieldStatsBinding
import com.decathlon.canaveral.stats.model.StatItem
import kotlin.math.roundToInt

class FieldsStatsAdapter: RecyclerView.Adapter<BaseViewHolder<StatItem>>() {

    private val itemList = ArrayList<StatItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: List<StatItem>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<StatItem> {
        return FieldStatItem(ItemListFieldStatsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        ))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<StatItem>, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class FieldStatItem(private val binding: ItemListFieldStatsBinding): BaseViewHolder<StatItem>(binding.root) {
        override fun bind(item: StatItem) {
            binding.fieldTitle.text = binding.root.resources.getString(item.title)
            binding.fieldValue.text = String.format("%d %%",(item.value as Float).roundToInt())
        }
    }
}