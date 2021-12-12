package com.decathlon.canaveral.game.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.canaveral.databinding.ItemListGamePlayerBinding
import java.util.*

class PlayersWaitingAdapter(private val startingPoints: Int, private val isBull25: Boolean,
                            private val inIndex: Int)
    : ListAdapter<Player, BaseViewHolder<*>>(PlayerDiffCallBack()) {

    constructor(isBull25: Boolean) : this(0, isBull25, 0)

    private var stackPoints: Stack<PlayerPoint>? = null

    fun setData(players: List<Player>, stack: Stack<PlayerPoint>?) {
        stackPoints = stack
        submitList(players)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return WaitingPlayerViewHolder(
            ItemListGamePlayerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        (holder as WaitingPlayerViewHolder).bind(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    inner class WaitingPlayerViewHolder(private var binding: ItemListGamePlayerBinding)
        : BaseViewHolder<Player>(binding.root) {

        override fun bind(item: Player) {
            binding.player = item
            binding.playerScore.text = if (startingPoints > 0) {
                startingPoints
                    .minus(DartsUtils.getPlayerScore(isBull25, item, stackPoints, inIndex))
                    .toString()
            } else {
                DartsUtils.getPlayerScore(isBull25, item, stackPoints, inIndex).toString()
            }
        }
    }

    private class PlayerDiffCallBack : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean =
            oldItem == newItem
    }
}
