package com.decathlon.canaveral.game

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.common.DartsUtils
import com.decathlon.canaveral.databinding.ItemListPlayerPointBinding
import com.decathlon.core.game.model.Dot
import com.decathlon.core.game.model.Point

class PlayerPointsAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {

    companion object {
        private const val TYPE_POINT = 0
        private const val TYPE_DOT = 1

        private const val DARTS_SHOTS_NUMBER = 3
    }

    private var listData = ArrayList<Point>(DARTS_SHOTS_NUMBER)
    private var isBlinkMode = false

    fun addData(point: Point) {
        listData.removeIf {
            it is Dot
        }
        if (listData.size < DARTS_SHOTS_NUMBER)  listData.add(point)
        addDots()
        notifyDataSetChanged()
    }

    fun removeLast() {
        listData.removeIf {
            it is Dot
        }
        if (listData.isNotEmpty()) listData.removeLast()
        addDots()
        notifyDataSetChanged()
    }

    fun setData(points: List<Point>, isLastDartBlink: Boolean) {
        isBlinkMode = isLastDartBlink
        listData.clear()
        listData.addAll(points)

        addDots()
        notifyDataSetChanged()
    }

    private fun addDots() {
        while (listData.size < DARTS_SHOTS_NUMBER) {
            listData.add(Dot())
        }
    }

    override fun getItemViewType(position: Int): Int {

        return when (listData[position]) {
            is Dot -> TYPE_DOT
            else -> TYPE_POINT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        return when (viewType) {
            TYPE_POINT -> {
                PointViewHolder(
                    ItemListPlayerPointBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false))

            }
            TYPE_DOT -> {
                DotViewHolder(
                    ItemListPlayerPointBinding.inflate(LayoutInflater.from(parent.context),
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
            is PointViewHolder -> holder.bind(element)
            is DotViewHolder -> holder.bind(element as Dot)
            else -> throw IllegalArgumentException()
        }
    }

    inner class DotViewHolder(private var binding: ItemListPlayerPointBinding)
        : BaseViewHolder<Dot>(binding.root) {

        override fun bind(item: Dot) {
            binding.point.text = item.value
        }
    }


   inner class PointViewHolder(private var binding: ItemListPlayerPointBinding) :
       BaseViewHolder<Point>(binding.root) {

           override fun bind(item: Point) {
               val blinkTextAnim = AnimationUtils.loadAnimation(itemView.context, R.anim.text_blink)
               if (isBlinkMode && !listData.contains(Dot()) && listData.indexOf(item) == 2) binding.point.startAnimation(blinkTextAnim)
               binding.point.text = DartsUtils.getStringFromPoint(binding.root.context, item)
               binding.executePendingBindings()
           }
   }

}
