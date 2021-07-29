package com.decathlon.canaveral.game.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.databinding.ItemListGamePlayerBinding
import java.util.*
import kotlin.collections.ArrayList

class PlayersWaitingAdapter(private val startingPoints: Int, private val isBull25: Boolean)
    : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var waitingPlayers = ArrayList<Player>()
    private var stackPoints: Stack<PlayerPoint>? = null

    fun setData(players: List<Player>, stack: Stack<PlayerPoint>?) {
        waitingPlayers.clear()
        waitingPlayers.addAll(players)
        stackPoints = stack
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return WaitingPlayerViewHolder(
            ItemListGamePlayerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        (holder as WaitingPlayerViewHolder).bind(waitingPlayers[position])
    }

    override fun getItemCount(): Int {
        return waitingPlayers.size
    }

    inner class WaitingPlayerViewHolder(private var binding: ItemListGamePlayerBinding)
        : BaseViewHolder<Player>(binding.root) {

        override fun bind(item: Player) {
            binding.player = item
            binding.playerScore.text = startingPoints
                .minus(DartsUtils.getPlayerScore(isBull25, item, stackPoints))
                .toString()
        }
    }
}
