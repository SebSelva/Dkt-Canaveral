package com.decathlon.canaveral.common.utils

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.model.NullPoint
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.model.Point
import timber.log.Timber
import java.util.*

class DartsUtils {

    companion object {

        const val DARTS_SHOTS_NUMBER = 3
        private const val BULL_VALUE = "Bull"

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

        private fun getScoreFromPoint(isSimpleBull25: Boolean, point: Point): Int {
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
                    else -> 0 // MISS OR NULL
                }
            }
            return score
        }

        fun getScoreFromPointList(pointList: List<Point>, isSimpleBull25: Boolean): Int {
            var score = 0
            for (point in pointList) {
                score += getScoreFromPoint(isSimpleBull25, point)
            }
            return score
        }

        fun getPlayerScore(isSimpleBull25: Boolean, player: Player, stackPoints: Stack<PlayerPoint>?,
            inValue: Int) :Int {
            var score = 0
            var isInCondition = false
            stackPoints?.filter { it.player == player && it.point !is NullPoint }
                ?.forEach {
                    isInCondition = isInCondition || isInOutConditionValid(it.point, inValue)
                    if (isInCondition && !it.isBusted) {
                        score += getScoreFromPoint(isSimpleBull25, it.point)
                    }
                }
            return score
        }

        private fun isInOutConditionValid(point: Point, inValue: Int): Boolean {
            return when (inValue) {
                1 -> point.isDoubled
                2 -> point.isTripled || point.value == BULL_VALUE
                else -> true
            }
        }

        fun isBusted (player: Player, stackPoints: Stack<PlayerPoint>, pointToAdd: Point,
                      startingPoints: Int, isBull25: Boolean, inIndex: Int, outIndex: Int): Boolean {
            val currentScore = startingPoints.minus(getPlayerScore(isBull25, player, stackPoints, inIndex))
            val pointValueToAdd = getScoreFromPoint(isBull25, pointToAdd)
            if (pointValueToAdd > currentScore)
                return true
            if (outIndex == 1 && (currentScore - pointValueToAdd) == 1)
                return true
            if (outIndex == 2 && ((currentScore - pointValueToAdd) == 1 || (currentScore - pointValueToAdd) == 2))
                return true
            if (pointValueToAdd == currentScore && !isInOutConditionValid(pointToAdd, outIndex))
                return true

            return false
        }

        /**
         * @return last darts thrown in the same round from @param stackPoints matching with @param player
         */
        fun getPlayerRoundDarts(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?): List<Point> {
            val lastPoints = ArrayList<Point>()
            stackPoints?.filter { it.player == currentPlayer && it.round == currentRound }
                ?.forEach {
                lastPoints.add(it.point)
            }
            return lastPoints
        }

        /**
         * @return last darts thrown in the same round from @param stackPoints matching with @param player
         */
        fun getPlayerLastValidRoundDarts(inIndex: Int, currentPlayer: Player, stackPoints: Stack<PlayerPoint>?): List<Point> {
            val lastPoints = ArrayList<Point>()
            var currentRound = 0
            var isValidPoint = false

            stackPoints?.filter { it.player == currentPlayer }?.forEach {
                if (it.round != currentRound) {
                    currentRound = it.round
                    lastPoints.clear()
                }
                isValidPoint = isValidPoint || isInOutConditionValid(it.point, inIndex)
                if (isValidPoint && !it.isBusted) lastPoints.add(it.point)
            }
            return lastPoints
        }

        /**
         * Get the round number for the next point to add
         */
        fun getRoundNumber(players: List<Player>, stackPoints: Stack<PlayerPoint>): Int {
            return (stackPoints.size / (players.size * DARTS_SHOTS_NUMBER)) + 1
        }

        fun isPlayerRoundComplete(currentPlayer: Player, round: Int, stack: Stack<PlayerPoint>): Boolean {
            return getPlayerRoundDarts(currentPlayer, round, stack).size == DARTS_SHOTS_NUMBER
        }

        fun getPlayerPPD(player: Player, stackPoints: Stack<PlayerPoint>, isBull25: Boolean): Float {
            val dartsNumber = stackPoints.filter { it.player == player && it.point !is NullPoint}.size
            return if (dartsNumber > 0) {
                    (getPlayerScore(isBull25, player, stackPoints, 0).toFloat() / dartsNumber.toFloat())
            } else {
                0F
            }
        }

        fun get01GamePlayerScore(startingPoints: Int, isBull25: Boolean, player: Player,
                                 stackPoints: Stack<PlayerPoint>?, inValue: Int) :Int {
            return startingPoints.minus(getPlayerScore(isBull25, player, stackPoints, inValue))
        }

        fun is01GameFinished(startingPoints: Int, isSimpleBull25: Boolean, nbRounds: Int?,
                             inIndex: Int, players: List<Player>, stackPoints: Stack<PlayerPoint>): Boolean {
            if (nbRounds != null && getRoundNumber(players, stackPoints) > nbRounds) {
                return true
            }
            getSorted01PlayersByScore(startingPoints, isSimpleBull25, inIndex, players, stackPoints).forEach {
                Timber.d("PLAYER : %s - %d", it.key.nickname, it.value)
                if (it.value == 0) return true
            }
            return false
        }

        private fun getSorted01PlayersByScore(startingPoints: Int, isSimpleBull25: Boolean,
                                              inIndex: Int, players: List<Player>,
                                              stackPoints: Stack<PlayerPoint>): Map<Player, Int> {
            val playersScoresMap = HashMap<Player,Int>()
            players.forEach {
                playersScoresMap[it] =
                    startingPoints - getPlayerScore(isSimpleBull25, it, stackPoints, inIndex)
            }

            Timber.d("PLAYERS STATE :")
            return playersScoresMap.toList().sortedBy { (_,value) -> value}.toMap()
        }

        fun isCountUpFinished(nbRounds: Int?, players: List<Player>,
                              stackPoints: Stack<PlayerPoint>): Boolean {
            if (nbRounds != null && getRoundNumber(players, stackPoints) > nbRounds) {
                return true
            }
            return false
        }
    }
}
