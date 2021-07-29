package com.decathlon.canaveral.game.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.common.model.X01Player
import com.decathlon.canaveral.databinding.ItemListPlayerGameStatsLargeBinding
import com.decathlon.canaveral.databinding.ItemListPlayerGameStatsMediumBinding
import com.decathlon.canaveral.databinding.ItemListPlayerGameStatsSmallBinding

class PlayersStatsAdapter(
    private val isWinViews: Boolean
): RecyclerView.Adapter<BaseViewHolder<X01Player>>()  {

    companion object {
        private const val SMALL = 0
        private const val MEDIUM = 1
        private const val LARGE = 2
    }

    private var players: List<X01Player> = emptyList()

    fun setData(playerList: List<X01Player>) {
        players = playerList
    }

    override fun getItemViewType(position: Int): Int {

        return if (isWinViews) {
            if (players.size < 3) LARGE else MEDIUM
        } else {
            if (players.size < 3) MEDIUM else SMALL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<X01Player> {

        return when (viewType) {
            SMALL -> {
                PlayerStatsViewSmallHolder(
                    ItemListPlayerGameStatsSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
            MEDIUM -> {
                PlayerStatsViewMediumHolder(
                    ItemListPlayerGameStatsMediumBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
            LARGE -> {
                PlayerStatsViewLargeHolder(
                    ItemListPlayerGameStatsLargeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<X01Player>, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int {
        return players.size
    }

    inner class PlayerStatsViewSmallHolder(private var binding: ItemListPlayerGameStatsSmallBinding) :
        BaseViewHolder<X01Player>(binding.root) {

        override fun bind(item: X01Player) {
            binding.player = item
            binding.playerStatsPpd.text = item.ppd.toString()
            binding.playerStatsSecondFieldTitle.text = binding.root.context.resources.getString(R.string.game_end_remaining)
            binding.playerStatsSecondFieldValue.text = item.remainingPoints.toString()
            binding.executePendingBindings()
        }
    }

    inner class PlayerStatsViewMediumHolder(private var binding: ItemListPlayerGameStatsMediumBinding) :
        BaseViewHolder<X01Player>(binding.root) {

        override fun bind(item: X01Player) {
            binding.player = item
            binding.playerStatsPpd.text = item.ppd.toString()
            binding.playerStatsSecondFieldTitle.text = binding.root.context.resources.getString(
                if (isWinViews) R.string.game_end_checkout else R.string.game_end_remaining)
            binding.playerStatsSecondFieldValue.text =
                if (isWinViews) {
                    item.checkout
                } else {
                    item.remainingPoints
                }.toString()
            binding.executePendingBindings()
        }
    }

    inner class PlayerStatsViewLargeHolder(private var binding: ItemListPlayerGameStatsLargeBinding) :
        BaseViewHolder<X01Player>(binding.root) {

        override fun bind(item: X01Player) {
            binding.player = item
            binding.playerStatsPpd.text = item.ppd.toString()
            binding.playerStatsSecondFieldTitle.text = binding.root.context.resources.getString(R.string.game_end_checkout)
            binding.playerStatsSecondFieldValue.text = item.checkout.toString()
            binding.executePendingBindings()
        }
    }
}