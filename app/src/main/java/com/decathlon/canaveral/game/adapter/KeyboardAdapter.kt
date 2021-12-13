package com.decathlon.canaveral.game.adapter

import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseViewHolder
import com.decathlon.canaveral.common.model.Point
import com.decathlon.canaveral.databinding.ItemKeyboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KeyboardAdapter(private val context: Context,
                      private val type: KeyboardType,
                      val onDartTouched: (Point) -> Unit,
                      val onDartTouchAborted: () -> Unit
                      ) : RecyclerView.Adapter<BaseViewHolder<*>>()
{
    private val isPortraitMode = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    private val keyboardItems : List<String> = if (isPortraitMode) {
        context.resources.getStringArray(R.array.keyboard_items)
    } else {
        context.resources.getStringArray(R.array.keyboard_items_land)
    }.asList()

    private val mBullValue = context.resources.getString(R.string.game_bull)
    private val mBackValue = context.resources.getString(R.string.game_back)
    private val mMissValue = context.resources.getString(R.string.game_miss)
    private val mDoubleValue = context.resources.getString(R.string.game_double)
    private val mTripleValue = context.resources.getString(R.string.game_triple)

    private var memoryKeyboardItem :AppCompatTextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return KeyboardItemViewHolder(
            ItemKeyboardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        (holder as KeyboardItemViewHolder).bind(keyboardItems[position])
    }

    override fun getItemCount(): Int = keyboardItems.size

    private fun getItemValue(itemValue: String): String {
        val cricketMissedValues = context.resources.getStringArray(R.array.keyboard_cricket_miss)
        val bullMissedValues = context.resources.getStringArray(R.array.keyboard_bull_miss)
        return when(type) {
            KeyboardType.NORMAL -> itemValue
            KeyboardType.CRICKET -> if (cricketMissedValues.contains(itemValue)) mMissValue else itemValue
            KeyboardType.BULL -> if (bullMissedValues.contains(itemValue)) mMissValue else itemValue
        }
    }

    private fun setDartTouchColors(view: AppCompatTextView?, isReleased :Boolean) {
        if (isReleased) {
            if (view?.text == mMissValue || view?.text == mBackValue) {
                view.apply {
                    setTextColor(AppCompatResources.getColorStateList(context, R.color.white))
                    setBackgroundColor(resources.getColor(R.color.grey_kb_background, null))
                }
            } else {
                view?.apply {
                    setTextColor(AppCompatResources.getColorStateList(context, R.color.grey_kb_text))
                    setBackgroundColor(resources.getColor(android.R.color.transparent, null))
                }
            }
        } else {
            view?.apply {
                setTextColor(AppCompatResources.getColorStateList(context, R.color.white))
                setBackgroundColor(resources.getColor(R.color.blue_dkt_secondary, null))
            }
        }
    }

    private fun onDartEvent(eventView: AppCompatTextView) {
        val value = eventView.tag as String
        val isDoubled = memoryKeyboardItem?.tag == mDoubleValue
        val isTripled = memoryKeyboardItem?.tag == mTripleValue
        when {
            value.isDigitsOnly() -> {
                handleTouchFeedback(eventView)
                onDartTouched(Point(value, isDoubled, isTripled))
            }
            value == mBullValue -> {
                if (memoryKeyboardItem?.tag != mTripleValue) {
                    onDartTouched(Point(mBullValue, isDoubled, isTripled = false))
                    handleTouchFeedback(eventView)
                }
            }
            value == mMissValue -> {
                onDartTouched(Point(mMissValue, isDoubled = false, isTripled = false))
                handleTouchFeedback(eventView)
            }
            value == mBackValue -> {
                onDartTouchAborted()
                handleTouchFeedback(eventView)
            }
            else -> {
                setDartTouchColors(eventView, false)
                setDartTouchColors(memoryKeyboardItem, true)
                memoryKeyboardItem = if (memoryKeyboardItem != eventView) { eventView } else { null }
            }
        }
    }

    private fun handleTouchFeedback(view: AppCompatTextView?) {
        setDartTouchColors(view, false)
        GlobalScope.launch(Dispatchers.Main) {
            delay(300)
            setDartTouchColors(view, true)
            setDartTouchColors(memoryKeyboardItem, true)
            memoryKeyboardItem = null
        }
    }

    inner class KeyboardItemViewHolder(private val binding: ItemKeyboardBinding):
        BaseViewHolder<String>(binding.root) {
            override fun bind(itemValue: String) {

                binding.keyboardTextview.apply {
                    typeface = Typeface.createFromAsset(context.assets, "klavika-medium.otf")
                    gravity = Gravity.CENTER
                    textSize = if (isPortraitMode) 25F else 18F
                    text = itemValue
                    setDartTouchColors(this, true)
                    tag = getItemValue(itemValue)
                    setOnClickListener { view ->
                        onDartEvent(view as AppCompatTextView)
                    }
                    isVisible = itemValue.isNotEmpty()
                }
            }
        }
}

enum class KeyboardType {
    NORMAL,
    CRICKET,
    BULL
}