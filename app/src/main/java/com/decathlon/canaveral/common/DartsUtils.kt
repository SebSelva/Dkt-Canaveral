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

        fun getPlayerScore(isSimpleBull25: Boolean, player: Player, stackPoints: Stack<PlayerPoint>?) :Int{
            var score = 0
            if (stackPoints != null) {
                for (playerPoint in stackPoints) {
                    if (playerPoint.player == player) {
                        score += getIntFromPoint(isSimpleBull25, playerPoint.point)
                    }
                }
            }
            return score
        }

        fun getPlayerLastDarts(currentPlayer: Player, stackPoints: Stack<PlayerPoint>?): List<Point> {
            val lastPoints = ArrayList<Point>()
            if (stackPoints?.isNotEmpty() == true) {
                for (playerPoint in stackPoints) {
                    if (playerPoint.player == currentPlayer) {
                        lastPoints.add(playerPoint.point)
                    } else {
                        lastPoints.clear()
                    }
                }
            }
            return lastPoints
        }

        fun isPlayerRoundComplete(currentPlayer: Player, stack: Stack<PlayerPoint>): Boolean {
            if (stack.size > 2) {
                val last3Darts = stack.subList(stack.size-3, stack.size)
                if (!last3Darts.isNullOrEmpty()) {
                    var player: Player? = null
                    for (playerPoint in last3Darts) {
                        if (player == null) player = playerPoint.player
                        else if (player != playerPoint.player) return false
                    }
                    return currentPlayer == player
                }
            }
            return false
        }
    }
}
