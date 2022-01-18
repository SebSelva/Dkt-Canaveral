package com.decathlon.canaveral.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.common.model.BaseItem
import com.decathlon.canaveral.common.model.Button
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.databinding.ItemListButtonBinding
import com.decathlon.canaveral.databinding.ItemListPlayerBinding
import timber.log.Timber

class PlayerAdapter(val maxPlayers :Int,
                    val addClickListener : (Player) -> Unit,
                    val delClickListener: (Player) -> Unit,
                    val editClickListener : (Player) -> Unit
) : ListAdapter<Player, BaseViewHolder<*>>(DiffCallback()) {

    companion object {
        private const val TYPE_PLAYER = 0
        private const val TYPE_BUTTON = 1
    }

    private var listData = ArrayList<BaseItem>()

    private fun addPlayer1() {
        addClickListener.invoke(Player())
    }

    fun setData(players: List<Player>) {
        listData.clear()
        if (players.isEmpty()) {
            addPlayer1()
        } else {
            listData.addAll(players)
        }

        if (listData.size < maxPlayers) {
            listData.add(
                listData.size, Button(
                    0,
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
        holder.setIsRecyclable(false)
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
                    addClickListener.invoke(Player())
                }
                binding.executePendingBindings()
            }
        }
    }


    inner class PlayerViewHolder(private var binding: ItemListPlayerBinding) :
        BaseViewHolder<Player>(binding.root) {

        override fun bind(item: Player) {
            binding.player = item

            Timber.d("bind item id:%d", item.id)
            // remove button
            if (bindingAdapterPosition == 0) {
                binding.optionBin.visibility = View.GONE
            }

            binding.optionEdit.setOnClickListener {
                editClickListener.invoke(item)
            }
            binding.optionBin.setOnClickListener {
                notifyItemRemoved(bindingAdapterPosition)
                Timber.d("remove item id:%d", item.id)
                listData.remove(item)
                delClickListener.invoke(item)

                if (itemCount == maxPlayers - 1 && listData.last() !is Button) {
                    listData.add(Button(0, ""))
                    notifyItemInserted(itemCount)
                }
            }
            binding.executePendingBindings()
        }

        private fun getPlayerNickname(player: Player) :String =
            if (player.nickname.isEmpty()) {
                binding.root.context.resources.getString(R.string.player_number, player.id)
            }
            else player.nickname
    }

    class DiffCallback : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }
    }

}
