package com.decathlon.canaveral.common.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.decathlon.canaveral.R

class KlavikaBITextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AppCompatTextView,
            0, 0).apply {

            try {
                typeface = Typeface.createFromAsset(context.assets, "klavika-bold-italic.otf")
            } finally {
                recycle()
            }
        }
    }
}