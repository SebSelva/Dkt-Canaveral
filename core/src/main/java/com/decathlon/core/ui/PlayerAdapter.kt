package com.decathlon.core.ui

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.core.R
import com.decathlon.core.databinding.ItemListButtonBinding
import com.decathlon.core.databinding.ItemListPlayerBinding
import com.decathlon.core.domain.model.BaseItem
import com.decathlon.core.domain.model.Button
import com.decathlon.core.domain.model.Player
import kotlinx.android.synthetic.main.item_list_button.*
import kotlin.coroutines.coroutineContext

class PlayerAdapter() : RecyclerView.Adapter<BaseViewHolder<*>>() {

    companion object {
        private const val TYPE_PLAYER = 0
        private const val TYPE_BUTTON = 1
    }

    private var listData = ArrayList<BaseItem>()

    init {
        setData()
    }

    private fun addPlayer() {

        val player1 = Player(
            1, "player1",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Flat_tick_icon.svg/1200px-Flat_tick_icon.svg.png",
            false
        )
        val newListData : List<Player>? = listOf(player1)
        listData.clear()
        if (newListData != null) {
            listData.addAll(newListData)
        }

    }

    private fun setData() {
        addPlayer()
        listData.add(
            listData.size, Button(
                12,
                ""
            )
        )
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        val comparable = listData[position]
        return when (comparable) {
            is Button -> TYPE_BUTTON
            is Player -> TYPE_PLAYER
            else -> throw IllegalArgumentException("Invalid type of data " + position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        return when (viewType) {
            TYPE_PLAYER -> {
                return PlayerViewHolder(
                        ItemListPlayerBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent, false))

            }
            TYPE_BUTTON -> {
                return  ButtonViewHolder(ItemListButtonBinding.inflate(LayoutInflater.from(parent.context),
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
            binding.btnAddPlayer.text = item.text
            binding.btnAddPlayer.setOnClickListener {
                notifyItemInserted(listData.size - 1)
                listData.add(listData.size - 1, Player(
                        1, "Player" + listData.size,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Flat_tick_icon.svg/1200px-Flat_tick_icon.svg.png", true
                ))
                println("dede")
                if (itemCount == 5) {
                    println("dedeINVISIBLE")
                    //binding.card.visibility = View.INVISIBLE
                    listData.remove(item)
                    notifyItemRemoved(adapterPosition)
                    println("dedeINVISIBLE@")

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
               if (!item.visibleIcon) {
                   binding.btnRemovePlayer.visibility = View.GONE
               }

               binding.btnRemovePlayer.setOnClickListener {
                   notifyItemRemoved(adapterPosition)
                   listData.remove(item)
                   println("dede item count $itemCount")
                   if (itemCount == 1) {
                   println("dedeVISIBLE")
                   listData.add(Button(2, ""))
                       notifyItemInserted(itemCount)
               }
               }

               binding.executePendingBindings()
           }


   }

}