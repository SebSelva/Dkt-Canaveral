package com.decathlon.canaveral.common

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.decathlon.canaveral.R
import com.decathlon.core.game.model.PlayerPoint
import com.decathlon.core.game.model.Point
import com.decathlon.core.player.model.Player
import java.util.*

class DartsUtils {

    companion object {

        const val BULL_VALUE = "Bull"
        const val MISS_VALUE = "Miss"

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

        private fun getIntFromPoint(isSimpleBull25: Boolean, point: Point): Int {
            var score :Int
            if (point.value.isDigitsOnly()) {
                score = point.value.toInt()
                score *= when {
                    point.isDoubled -> 2
                    point.isTripled -> 3
                    else -> 1
                }
            } else {
                score = when (point.value) {
                    BULL_VALUE -> if (isSimpleBull25 && !point.isDoubled) { 25 } else { 50 }
                    else -> 0 // MISS
                }
            }
            return score
        }

        fun getScoreFromPointList(pointList: List<Point>, isSimpleBull25: Boolean): Int {
            var score = 0
            for (point in pointList) {
                score += getIntFromPoint(isSimpleBull25, point)
            }
            return score
        }

        fun getPlayerScore(isSimpleBull25: Boolean, player: Player, stackPoints: Stack<PlayerPoint>?) :Int{
            var score = 0
            stackPoints?.forEach {
                if (it.player == player) {
                    score += getIntFromPoint(isSimpleBull25, it.point)
                }
            }
            return score
        }

        fun getPlayerLastDarts(currentPlayer: Player, stackPoints: Stack<PlayerPoint>?): List<Point> {
            val lastPoints = ArrayList<Point>()
            stackPoints?.forEach {
                if (it.player == currentPlayer) {
                    if (lastPoints.size == 3) lastPoints.clear()
                    lastPoints.add(it.point)
                } else {
                    lastPoints.clear()
                }
            }
            return lastPoints
        }

        fun isPlayerRoundComplete(currentPlayer: Player, stack: Stack<PlayerPoint>): Boolean {
            return getPlayerLastDarts(currentPlayer, stack).size == 3
        }
    }
}
