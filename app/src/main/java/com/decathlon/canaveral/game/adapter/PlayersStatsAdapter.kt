package com.decathlon.canaveral.game.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.common.model.X01PlayerStats
import com.decathlon.canaveral.databinding.ItemListPlayerGameStatsLargeBinding
import com.decathlon.canaveral.databinding.ItemListPlayerGameStatsMediumBinding
import com.decathlon.canaveral.databinding.ItemListPlayerGameStatsSmallBinding
import java.util.*

class PlayersStatsAdapter(
    private val isWinViews: Boolean
): RecyclerView.Adapter<BaseViewHolder<X01PlayerStats>>()  {

    companion object {
        private const val SMALL = 0
        private const val MEDIUM = 1
        private const val LARGE = 2
    }

    private var players: List<X01PlayerStats> = emptyList()
    private var isPortraitMode = true

    fun setData(playerList: List<X01PlayerStats>, isPortrait: Boolean) {
        players = playerList
        isPortraitMode = isPortrait
    }

    override fun getItemViewType(position: Int): Int {

        return if (isWinViews) {
            if (players.size > 2 && isPortraitMode) MEDIUM else LARGE
        } else {
            if (players.size > 2 && isPortraitMode) SMALL else MEDIUM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<X01PlayerStats> {

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

    override fun onBindViewHolder(holder: BaseViewHolder<X01PlayerStats>, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int {
        return players.size
    }

    inner class PlayerStatsViewSmallHolder(private var binding: ItemListPlayerGameStatsSmallBinding) :
        BaseViewHolder<X01PlayerStats>(binding.root) {

        override fun bind(item: X01PlayerStats) {
            binding.player = item.player
            binding.playerStatsPpd.text = String.format(Locale.ENGLISH,
                binding.root.context.resources.getString(R.string.game_end_ppd_value), item.ppd)
            binding.playerStatsSecondFieldTitle.text = binding.root.context.resources.getString(R.string.game_end_remaining)
            binding.playerStatsSecondFieldValue.text = item.currentScore.toString()
            binding.executePendingBindings()
        }
    }

    inner class PlayerStatsViewMediumHolder(private var binding: ItemListPlayerGameStatsMediumBinding) :
        BaseViewHolder<X01PlayerStats>(binding.root) {

        override fun bind(item: X01PlayerStats) {
            binding.player = item.player
            binding.playerStatsPpd.text = String.format(Locale.ENGLISH,
                binding.root.context.resources.getString(R.string.game_end_ppd_value), item.ppd)
            binding.playerStatsSecondFieldTitle.text = binding.root.context.resources.getString(
                if (isWinViews) R.string.game_end_checkout else R.string.game_end_remaining)
            binding.playerStatsSecondFieldValue.text =
                if (isWinViews) {
                    item.checkout
                } else {
                    item.currentScore
                }.toString()
            binding.executePendingBindings()
        }
    }

    inner class PlayerStatsViewLargeHolder(private var binding: ItemListPlayerGameStatsLargeBinding) :
        BaseViewHolder<X01PlayerStats>(binding.root) {

        override fun bind(item: X01PlayerStats) {
            binding.player = item.player
            binding.playerStatsPpd.text = String.format(Locale.ENGLISH,
                binding.root.context.resources.getString(R.string.game_end_ppd_value), item.ppd)
            binding.playerStatsSecondFieldTitle.text = binding.root.context.resources.getString(R.string.game_end_checkout)
            binding.playerStatsSecondFieldValue.text = item.checkout.toString()
            binding.executePendingBindings()
        }
    }
}