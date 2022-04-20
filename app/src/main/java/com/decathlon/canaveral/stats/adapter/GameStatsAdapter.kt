package com.decathlon.canaveral.stats.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.databinding.ItemListGameStatsBinding
import com.decathlon.canaveral.stats.model.GameStats

class GameStatsAdapter : RecyclerView.Adapter<BaseViewHolder<GameStats>>()  {

    private val gameStatsItems = ArrayList<GameStats>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: List<GameStats>) {
        gameStatsItems.clear()
        gameStatsItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<GameStats> {
        return GameStatsItem(ItemListGameStatsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<GameStats>, position: Int) {
        holder.bind(gameStatsItems[position])
    }

    override fun getItemCount(): Int {
        return gameStatsItems.size
    }

    inner class GameStatsItem(private var binding: ItemListGameStatsBinding)
        : BaseViewHolder<GameStats>(binding.root) {
        override fun bind(item: GameStats) {
            binding.gameStatsTitle.text = binding.root.resources.getString(item.title)
            val gameStatsAdapter = GameStatDetailsAdapter()
            binding.gameStatsDetails.adapter = gameStatsAdapter
            binding.gameStatsDetails.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            gameStatsAdapter.setData(item.statList)
        }
    }
}