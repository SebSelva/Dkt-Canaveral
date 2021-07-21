package com.decathlon.canaveral.game

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.isDigitsOnly
import com.decathlon.canaveral.common.model.Point
import kotlinx.coroutines.*
import com.decathlon.canaveral.R as RApp
import com.decathlon.core.R as RCore

class KeyboardAdapter(private val context: Context,
                      val onDartTouched: (Point) -> Unit,
                      val onDartTouchAborted: () -> Unit
                      ) : BaseAdapter()
{
    private val keyboardItems : List<String> = context.resources.getStringArray(RCore.array.keyboard_items).asList()

    private val mBullValue = context.resources.getString(RApp.string.game_bull)
    private val mBackValue = context.resources.getString(RApp.string.game_back)
    private val mMissValue = context.resources.getString(RApp.string.game_miss)
    private val mDoubleValue = context.resources.getString(RApp.string.game_double)
    private val mTripleValue = context.resources.getString(RApp.string.game_triple)

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
            val layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(RApp.layout.item_keyboard, null);
        }

        val itemText = view?.findViewById<AppCompatTextView>(RApp.id.keyboard_textview)
        itemText?.apply {
            height = context.resources.getDimension(RApp.dimen.keyboard_item_height).toInt()
            width = context.resources.getDimension(RApp.dimen.keyboard_item_width).toInt()
            typeface = Typeface.createFromAsset(context.assets, "klavika-bold.otf")
            gravity = Gravity.CENTER
            tag = itemValue
            setOnClickListener { view ->
                onDartEvent(view as AppCompatTextView)
            }
            setDartTouchColors(this, true)
        }
        when (itemValue) {
            mBullValue -> itemText?.textSize = 25F
            mBackValue, mMissValue -> {
                itemText?.textSize = 21F
            }
            else -> itemText?.textSize = 35F
        }
        itemText?.text = itemValue
        return view!!
    }

    private fun setDartTouchColors(view: AppCompatTextView?, isReleased :Boolean) {
        if (isReleased) {
            if (view?.tag == mMissValue || view?.tag == mBackValue) {
                view.apply {
                    setTextColor(AppCompatResources.getColorStateList(context, RApp.color.white))
                    background = AppCompatResources.getDrawable(context, RApp.drawable.keyboard_background_grey)
                }
            } else {
                view?.apply {
                    setTextColor(AppCompatResources.getColorStateList(context, RApp.color.grey_kb_text))
                    background = null
                }
            }
        } else {
            view?.apply {
                setTextColor(AppCompatResources.getColorStateList(context, RApp.color.white))
                background = AppCompatResources.getDrawable(context, RApp.drawable.keyboard_background_blue)
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
