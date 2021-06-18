package com.decathlon.canaveral.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.databinding.ItemListButtonBinding
import com.decathlon.canaveral.databinding.ItemListPlayerBinding
import com.decathlon.core.player.model.BaseItem
import com.decathlon.core.player.model.Button
import com.decathlon.core.player.model.Player

class PlayerAdapter(val maxPlayers :Int,
                    val addClickListener : (Player) -> Unit,
                    val delClickListener: (Player) -> Unit) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    companion object {
        private const val TYPE_PLAYER = 0
        private const val TYPE_BUTTON = 1
    }

    private var listData = ArrayList<BaseItem>()

    private fun addPlayer1() {

        val player1 = Player(
            1, "Player 1", "null", "Null",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Flat_tick_icon.svg/1200px-Flat_tick_icon.svg.png"
        )
        addClickListener.invoke(player1)
        val newListData : List<Player> = listOf(player1)
        listData.clear()
        listData.addAll(newListData)
    }

    fun setData(players: List<Player>) {
        if (players.isEmpty()) {
            addPlayer1()
        } else {
            listData.clear()
            listData.addAll(players)
        }

        if (listData.size < maxPlayers) {
            listData.add(
                listData.size, Button(
                    maxPlayers,
                    ""
                )
            )
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        return when (listData[position]) {
            is Button -> TYPE_BUTTON
            is Player -> TYPE_PLAYER
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        return when (viewType) {
            TYPE_PLAYER -> {
                PlayerViewHolder(
                    ItemListPlayerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false))

            }
            TYPE_BUTTON -> {
                ButtonViewHolder(ItemListButtonBinding.inflate(LayoutInflater.from(parent.context),
                    parent, false))
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }

    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {

        val element = listData[position]
        when (holder) {
            is PlayerViewHolder -> holder.bind(element as Player)
            is ButtonViewHolder -> holder.bind(element as Button)
            else -> throw IllegalArgumentException()
        }
    }

    inner class ButtonViewHolder(private var binding: ItemListButtonBinding)
        : BaseViewHolder<Button>(binding.root) {

        override fun bind(item: Button) {
            binding.btnAddPlayer.setOnClickListener {
                if (listData.size <= maxPlayers) {
                    val player = Player(
                        listData.size, "Player " + listData.size, "null", "Null",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Flat_tick_icon.svg/1200px-Flat_tick_icon.svg.png"
                    )
                    addClickListener.invoke(player)
                }
                binding.executePendingBindings()
            }
        }
    }


   inner class PlayerViewHolder(private var binding: ItemListPlayerBinding) :
       BaseViewHolder<Player>(binding.root) {

           override fun bind(item: Player) {
               binding.player = item
               // remove button
               if (position == 0) {
                   binding.btnRemovePlayer.visibility = View.GONE
               }

               binding.btnRemovePlayer.setOnClickListener {
                   notifyItemRemoved(adapterPosition)
                   listData.remove(item)
                   delClickListener.invoke(item)

                   if (itemCount == maxPlayers - 1 && listData.last() !is Button) {
                       listData.add(Button(maxPlayers, ""))
                           notifyItemInserted(itemCount)
                   }
               }
               binding.executePendingBindings()
           }
   }

}