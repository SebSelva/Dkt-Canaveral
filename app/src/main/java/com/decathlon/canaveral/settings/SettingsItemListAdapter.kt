package com.decathlon.canaveral.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.databinding.ListTextviewSettingsItemBinding

class SettingsItemListAdapter(val onItemClickListener : (Int) -> Unit): RecyclerView.Adapter<BaseViewHolder<String>>() {

    private var stringItemList = ArrayList<String>()
    private var stringItemSelected = String()

    fun setData(itemList: List<String>, itemSelected: String) {
        stringItemList.clear()
        stringItemList.addAll(itemList)
        stringItemSelected = itemSelected
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String> {
        return TextViewHolder(
            ListTextviewSettingsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<String>, position: Int) {
        holder.bind(stringItemList[position])
    }

    override fun getItemCount(): Int {
        return stringItemList.size
    }

    inner class TextViewHolder(private var binding: ListTextviewSettingsItemBinding)
        : BaseViewHolder<String>(binding.root) {

        override fun bind(item: String) {
            binding.itemText.text = item
            binding.itemText.setOnClickListener {
                stringItemSelected = item
                onItemClickListener.invoke(position)
                notifyDataSetChanged()
            }
            binding.itemText.isSelected = (stringItemSelected == item)
            binding.executePendingBindings()
        }
    }
}