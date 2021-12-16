package com.decathlon.canaveral.game.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.common.model.PlayerStats
import com.decathlon.canaveral.databinding.*
import java.util.*

class PlayersStatsAdapter(
    private val gameTypeIndex: Int,
    private val isWinViews: Boolean,
    private val isDraw: Boolean
): RecyclerView.Adapter<BaseViewHolder<PlayerStats>>() {

    companion object {
        private const val SMALL = 0
        private const val LARGE = 1
        private const val WIN_SMALL = 2
        private const val WIN_LARGE = 3
    }

    private var players: List<PlayerStats> = emptyList()
    private var isPortraitMode = true

    fun setData(playerList: List<PlayerStats>, isPortrait: Boolean) {
        players = playerList
        isPortraitMode = isPortrait
    }

    override fun getItemViewType(position: Int): Int {

        return if (isWinViews) {
            if (players.size > 4 && isPortraitMode) WIN_SMALL else WIN_LARGE
        } else {
            if (players.size > 4 && isPortraitMode) SMALL else LARGE
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
            LARGE -> {
                PlayerStatsViewLargeHolder(
                    ItemListPlayerGameStatsLargeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
            WIN_SMALL -> {
                WinnerStatsViewSmallHolder(
                    ItemListWinnerGameStatsSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
            WIN_LARGE -> {
                WinnerStatsViewLargeHolder(
                    ItemListWinnerGameStatsLargeBinding.inflate(
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

    inner class WinnerStatsViewSmallHolder(private var binding: ItemListWinnerGameStatsSmallBinding) :
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

    inner class WinnerStatsViewLargeHolder(private var binding: ItemListWinnerGameStatsLargeBinding) :
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
                if (isDraw || !isWinViews) R.string.game_end_remaining else R.string.game_end_checkout
            }
            context.resources.getString(R.string.game_type_count_up) -> {
                R.string.game_end_score_reached
            }
            else -> null
        }
        return if (stringResId != null) context.resources.getString(stringResId) else ""
    }

    private fun getSecondFieldValue(context: Context, playerStats: PlayerStats): String {
        return when (context.resources.getStringArray(R.array.game_type_array)[gameTypeIndex]) {
            context.resources.getString(R.string.game_type_01_game) -> {
                if (isDraw || !isWinViews) { playerStats.currentScore } else { playerStats.checkout }.toString()
            }
            context.resources.getString(R.string.game_type_count_up) -> {
                playerStats.currentScore.toString()
            }
            else -> null
        } ?: ""
    }
}