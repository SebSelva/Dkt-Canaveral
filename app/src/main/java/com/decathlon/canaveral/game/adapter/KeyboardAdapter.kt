package com.decathlon.canaveral.game.adapter

import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import com.decathlon.canaveral.common.model.Point
import kotlinx.coroutines.*
import com.decathlon.canaveral.R

class KeyboardAdapter(private val context: Context,
                      private val type: KeyboardType,
                      val onDartTouched: (Point) -> Unit,
                      val onDartTouchAborted: () -> Unit
                      ) : BaseAdapter()
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

    override fun getCount(): Int = keyboardItems.size

    override fun getItem(position: Int): Any {
        return keyboardItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemValue = keyboardItems[position]
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_keyboard, null)
        }

        val itemText = view?.findViewById<AppCompatTextView>(R.id.keyboard_textview)
        itemText?.apply {
            height = if (isPortraitMode)
                context.resources.getDimension(R.dimen.keyboard_item_height).toInt()
            else
                context.resources.getDimension(R.dimen.keyboard_item_land_height).toInt()

            width = if (isPortraitMode)
                context.resources.getDimension(R.dimen.keyboard_item_width).toInt()
            else
                context.resources.getDimension(R.dimen.keyboard_item_land_width).toInt()

            typeface = Typeface.createFromAsset(context.assets, "klavika-bold.otf")
            gravity = Gravity.CENTER
            text = itemValue
            tag = getItemValue(itemValue)
            setOnClickListener { view ->
                onDartEvent(view as AppCompatTextView)
            }
            isVisible = itemValue.isNotEmpty()
            setDartTouchColors(this, true)
        }
        itemText?.textSize = when (itemValue) {
            mBullValue -> if (isPortraitMode) 25F else 22F
            mBackValue, mMissValue -> if (isPortraitMode) 21F else 18F
            else -> if (isPortraitMode) 35F else 32F
        }
        return view!!
    }

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
                    background = AppCompatResources.getDrawable(context, R.drawable.keyboard_background_grey)
                }
            } else {
                view?.apply {
                    setTextColor(AppCompatResources.getColorStateList(context, R.color.grey_kb_text))
                    background = null
                }
            }
        } else {
            view?.apply {
                setTextColor(AppCompatResources.getColorStateList(context, R.color.white))
                background = AppCompatResources.getDrawable(context, R.drawable.keyboard_background_blue)
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
                setDartTouchColors (eventView, false)
                setDartTouchColors (memoryKeyboardItem, true)
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
}

enum class KeyboardType {
    NORMAL,
    CRICKET,
    BULL
}