package com.decathlon.canaveral.game.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.common.model.PlayerStats
import com.decathlon.canaveral.databinding.ItemListPlayerGameStatsLargeBinding
import com.decathlon.canaveral.databinding.ItemListPlayerGameStatsMediumBinding
import com.decathlon.canaveral.databinding.ItemListPlayerGameStatsSmallBinding
import java.util.*

class PlayersStatsAdapter(
    private val gameTypeIndex: Int,
    private val isWinViews: Boolean
): RecyclerView.Adapter<BaseViewHolder<PlayerStats>>()  {

    companion object {
        private const val SMALL = 0
        private const val MEDIUM = 1
        private const val LARGE = 2
    }

    private var players: List<PlayerStats> = emptyList()
    private var isPortraitMode = true

    fun setData(playerList: List<PlayerStats>, isPortrait: Boolean) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PlayerStats> {

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

    override fun onBindViewHolder(holder: BaseViewHolder<PlayerStats>, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int {
        return players.size
    }

    inner class PlayerStatsViewSmallHolder(private var binding: ItemListPlayerGameStatsSmallBinding) :
        BaseViewHolder<PlayerStats>(binding.root) {

        override fun bind(item: PlayerStats) {
            binding.player = item.player
            binding.playerStatsPpd.text = String.format(Locale.ENGLISH,
                binding.root.context.resources.getString(R.string.game_end_ppd_value), item.ppd)
            binding.playerStatsSecondFieldTitle.text = getSecondFieldTitle(binding.root.context)
            binding.playerStatsSecondFieldValue.text = getSecondFieldValue(binding.root.context, item)
            binding.executePendingBindings()
        }
    }

    inner class PlayerStatsViewMediumHolder(private var binding: ItemListPlayerGameStatsMediumBinding) :
        BaseViewHolder<PlayerStats>(binding.root) {

        override fun bind(item: PlayerStats) {
            binding.player = item.player
            binding.playerStatsPpd.text = String.format(Locale.ENGLISH,
                binding.root.context.resources.getString(R.string.game_end_ppd_value), item.ppd)
            binding.playerStatsSecondFieldTitle.text = getSecondFieldTitle(binding.root.context)
            binding.playerStatsSecondFieldValue.text = getSecondFieldValue(binding.root.context, item)
            binding.executePendingBindings()
        }
    }

    inner class PlayerStatsViewLargeHolder(private var binding: ItemListPlayerGameStatsLargeBinding) :
        BaseViewHolder<PlayerStats>(binding.root) {

        override fun bind(item: PlayerStats) {
            binding.player = item.player
            binding.playerStatsPpd.text = String.format(Locale.ENGLISH,
                binding.root.context.resources.getString(R.string.game_end_ppd_value), item.ppd)
            binding.playerStatsSecondFieldTitle.text = getSecondFieldTitle(binding.root.context)
            binding.playerStatsSecondFieldValue.text = getSecondFieldValue(binding.root.context, item)
            binding.executePendingBindings()
        }
    }

    private fun getSecondFieldTitle(context: Context): String {
        val stringResId = when(context.resources.getStringArray(R.array.game_type_array)[gameTypeIndex]) {
            context.resources.getString(R.string.game_type_01_game) -> {
                if (isWinViews) R.string.game_end_checkout else R.string.game_end_remaining
            }
            context.resources.getString(R.string.game_type_count_up) -> {
                if (isWinViews) R.string.game_end_score_total else R.string.game_end_score_reached
            }
            else -> null
        }
        return if (stringResId != null) context.resources.getString(stringResId) else ""
    }

    private fun getSecondFieldValue(context: Context, playerStats: PlayerStats): String {
        return when (context.resources.getStringArray(R.array.game_type_array)[gameTypeIndex]) {
            context.resources.getString(R.string.game_type_01_game) -> {
                if (isWinViews) { playerStats.checkout } else { playerStats.currentScore }.toString()
            }
            context.resources.getString(R.string.game_type_count_up) -> {
                playerStats.currentScore.toString()
            }
            else -> null
        } ?: ""
    }
}