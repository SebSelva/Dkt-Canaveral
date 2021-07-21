package com.decathlon.canaveral.common

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.model.Point
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

        /**
         * @return last darts thrown in the same round from @param stackPoints matching with @param player
         */
        fun getPlayerLastDarts(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?): List<Point> {
            val lastPoints = ArrayList<Point>()
            stackPoints?.forEach {
                if (it.player == currentPlayer && it.round == currentRound) {
                    lastPoints.add(it.point)
                } else {
                    lastPoints.clear()
                }
            }
            return lastPoints
        }

        /**
         * Get the round number for the next point to add
         */
        fun getRoundNumber(players: List<Player>, stackPoints: Stack<PlayerPoint>): Int {
            return (stackPoints.size / (players.size * 3)) + 1
        }

        fun isPlayerRoundComplete(currentPlayer: Player, round: Int, stack: Stack<PlayerPoint>): Boolean {
            return getPlayerLastDarts(currentPlayer, round, stack).size == 3
        }
    }
}
