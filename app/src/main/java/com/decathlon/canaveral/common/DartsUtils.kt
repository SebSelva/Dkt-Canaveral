package com.decathlon.canaveral.common

import android.content.Context
import com.decathlon.canaveral.R
import com.decathlon.core.game.model.Point

class DartsUtils {

    companion object {

        fun getStringFromPoint(context: Context, point: Point) :String {
            var text = ""
            if (point.isDoubled) {
                text += context.resources.getString(R.string.game_double)
            } else if (point.isTripled) {
                text += context.resources.getString(R.string.game_triple)
            }
            text += point.value
            return text
        }
    }
}
