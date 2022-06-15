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
import kotlin.math.roundToInt

class DartsUtils {

    companion object {

        const val DARTS_SHOTS_NUMBER = 3
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
        fun getPlayerPointRoundDarts(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?): List<PlayerPoint>? {
            return stackPoints?.filter { it.player == currentPlayer && it.round == currentRound }
        }

        /**
         * @return darts thrown from @param stackPoints matching with @param player
         */
        fun getPlayerDarts(currentPlayer: Player, stackPoints: Stack<PlayerPoint>?): List<PlayerPoint> {
            val lastPoints = ArrayList<PlayerPoint>()
            stackPoints?.filter { it.player == currentPlayer}
                ?.forEach {
                lastPoints.add(it)
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

        fun getCountUpPlayerScore(isBull25: Boolean, player: Player,
                                  stackPoints: Stack<PlayerPoint>?) :Int {
            return getPlayerScore(isBull25, player, stackPoints, 0)
        }

        fun isCountUpFinished(nbRounds: Int?, players: List<Player>,
                              stackPoints: Stack<PlayerPoint>): Boolean {
            if (nbRounds != null && getRoundNumber(players, stackPoints) > nbRounds) {
                return true
            }
            return false
        }

        fun getIntValue(value: Any): Int {
            return when (value) {
                is Int -> value
                is Long -> value.toInt()
                is Float -> value.roundToInt()
                else -> 0
            }
        }

        fun getRate(statPart: Long, statTotal: Long) =
            if (statTotal > 0) statPart * 100F / statTotal else 0F

        fun getIntPercentValue(value: Float) =
            String.format("%d %%", value.roundToInt())

        fun getArrayLevel(levelResInt: Int): Int {
            return when (levelResInt) {
                R.string.stats_trick_baby_ton,
                R.string.stats_trick_bag_o_nuts,
                R.string.stats_trick_bulls_eye,
                R.string.stats_trick_bust,
                R.string.stats_trick_hattrick,
                R.string.stats_trick_high_ton,
                R.string.stats_trick_low_ton,
                R.string.stats_trick_three_in_a_bed,
                R.string.stats_trick_ton,
                R.string.stats_trick_ton_80,
                R.string.stats_trick_white_horse,
                R.string.stats_trick_round_score_60_and_more,
                R.string.stats_trick_round_score_100_and_more,
                R.string.stats_trick_round_score_140_and_more,
                R.string.stats_trick_round_score_180,
                R.string.stats_trick_field_15,
                R.string.stats_trick_field_16,
                R.string.stats_trick_field_17,
                R.string.stats_trick_field_18,
                R.string.stats_trick_field_19,
                R.string.stats_trick_field_20,
                R.string.stats_trick_field_bull -> R.array.trick_levels

                R.string.stats_trick_highest_score_round -> R.array.high_round_levels

                R.string.stats_trick_avg_score_dart1,
                R.string.stats_trick_avg_score_dart2,
                R.string.stats_trick_avg_score_dart3 -> R.array.average_dart_levels

                R.string.stats_trick_avg_score_round -> R.array.average_round_score_levels

                R.string.stats_trick_checkout_rate -> R.array.rate_levels

                R.string.stats_trick_highest_checkout -> R.array.highest_checkout_levels

                R.string.stats_trick_highest_8rounds -> R.array.highest_score_8rounds_levels
                R.string.stats_trick_highest_12rounds -> R.array.highest_score_12rounds_levels
                R.string.stats_trick_highest_16rounds -> R.array.highest_score_16rounds_levels

                else -> R.array.trick_levels
            }
        }

        fun getStatDescription(trickTitle: Int): Int? {
            val descriptionMap = mapOf(R.string.stats_trick_baby_ton to R.string.stats_trick_baby_ton_info,
            R.string.stats_trick_bag_o_nuts to R.string.stats_trick_bag_o_nuts_info,
            R.string.stats_trick_bulls_eye to R.string.stats_trick_bulls_eye_info,
            R.string.stats_trick_bust to R.string.stats_trick_bust_info,
            R.string.stats_trick_hattrick to R.string.stats_trick_hattrick_info,
            R.string.stats_trick_high_ton to R.string.stats_trick_high_ton_info,
            R.string.stats_trick_low_ton to R.string.stats_trick_low_ton_info,
            R.string.stats_trick_three_in_a_bed to R.string.stats_trick_three_in_a_bed_info,
            R.string.stats_trick_ton to R.string.stats_trick_ton_info,
            R.string.stats_trick_ton_80 to R.string.stats_trick_ton_80_info,
            R.string.stats_trick_white_horse to R.string.stats_trick_white_horse_info,
            R.string.stats_trick_round_score_60_and_more to R.string.stats_trick_round_score_60_and_more_info,
            R.string.stats_trick_round_score_100_and_more to R.string.stats_trick_round_score_100_and_more_info,
            R.string.stats_trick_round_score_140_and_more to R.string.stats_trick_round_score_140_and_more_info,
            R.string.stats_trick_round_score_180 to R.string.stats_trick_round_score_180_info,

            R.string.stats_trick_field_15 to R.string.stats_trick_field_15_info,
            R.string.stats_trick_field_16 to R.string.stats_trick_field_16_info,
            R.string.stats_trick_field_17 to R.string.stats_trick_field_17_info,
            R.string.stats_trick_field_18 to R.string.stats_trick_field_18_info,
            R.string.stats_trick_field_19 to R.string.stats_trick_field_19_info,
            R.string.stats_trick_field_20 to R.string.stats_trick_field_20_info,
            R.string.stats_trick_field_bull to R.string.stats_trick_field_bull_info,

            R.string.stats_trick_highest_score_round to R.string.stats_trick_highest_score_round_info,
            R.string.stats_trick_avg_score_dart1 to R.string.stats_trick_avg_score_dart1_info,
            R.string.stats_trick_avg_score_dart2 to R.string.stats_trick_avg_score_dart2_info,
            R.string.stats_trick_avg_score_dart3 to R.string.stats_trick_avg_score_dart3_info,
            R.string.stats_trick_checkout_rate to R.string.stats_trick_checkout_rate_info,
            R.string.stats_trick_highest_checkout to R.string.stats_trick_highest_checkout_info,
            R.string.stats_trick_highest_8rounds to R.string.stats_trick_highest_8rounds_info,
            R.string.stats_trick_highest_12rounds to R.string.stats_trick_highest_12rounds_info,
            R.string.stats_trick_highest_16rounds to R.string.stats_trick_highest_16rounds_info,
            R.string.stats_trick_avg_score_round to R.string.stats_trick_avg_score_round_info)
            return descriptionMap[trickTitle]
        }

        /**
         * Tricks on a round
         * */
        fun isBabyTon(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?, isSimpleBull25: Boolean): Boolean {
            return getScoreFromPointList(getPlayerRoundDarts(currentPlayer, currentRound, stackPoints), isSimpleBull25) == 95
        }

        fun isBagONuts(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?, isSimpleBull25: Boolean): Boolean {
            return getScoreFromPointList(getPlayerRoundDarts(currentPlayer, currentRound, stackPoints), isSimpleBull25) == 45
        }

        fun isHatTrick(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?): Boolean {
            var isHatTrick = true
            for (point in getPlayerRoundDarts(currentPlayer, currentRound, stackPoints)) {
                isHatTrick = isHatTrick && (point.isDoubled && point.value == BULL_VALUE)
            }
            return isHatTrick
        }

        fun isHighTon(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?, isSimpleBull25: Boolean): Boolean {
            return getScoreFromPointList(getPlayerRoundDarts(currentPlayer, currentRound, stackPoints), isSimpleBull25) in 151..180
        }

        fun isLowTon(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?, isSimpleBull25: Boolean): Boolean {
            return getScoreFromPointList(getPlayerRoundDarts(currentPlayer, currentRound, stackPoints), isSimpleBull25) in 101..150
        }

        fun isThreeInABed(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?): Boolean {
            var isTrickValid = true
            val roundPoints = getPlayerRoundDarts(currentPlayer, currentRound, stackPoints)
            for (point in roundPoints) {
                isTrickValid = isTrickValid && (point.value == roundPoints.first().value)
            }
            return isTrickValid
        }

        fun isTon(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?, isSimpleBull25: Boolean): Boolean {
            return getScoreFromPointList(getPlayerRoundDarts(currentPlayer, currentRound, stackPoints), isSimpleBull25) == 100
        }

        fun isTon80(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?, isSimpleBull25: Boolean): Boolean {
            return getScoreFromPointList(getPlayerRoundDarts(currentPlayer, currentRound, stackPoints), isSimpleBull25) == 80
        }

        fun isWhiteHorse(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?): Boolean {
            var isTrickValid = true
            val roundPoints = getPlayerRoundDarts(currentPlayer, currentRound, stackPoints)
            for (point in roundPoints) {
                isTrickValid = isTrickValid && point.isTripled
            }
            return isTrickValid
        }

        fun isMoreThan60(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?, isSimpleBull25: Boolean): Boolean {
            return getScoreFromPointList(getPlayerRoundDarts(currentPlayer, currentRound, stackPoints), isSimpleBull25) in 60..99
        }

        fun isMoreThan100(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?, isSimpleBull25: Boolean): Boolean {
            return getScoreFromPointList(getPlayerRoundDarts(currentPlayer, currentRound, stackPoints), isSimpleBull25) in 100..139
        }

        fun isMoreThan140(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?, isSimpleBull25: Boolean): Boolean {
            return getScoreFromPointList(getPlayerRoundDarts(currentPlayer, currentRound, stackPoints), isSimpleBull25) in 140..179
        }

        fun is180(currentPlayer: Player, currentRound: Int, stackPoints: Stack<PlayerPoint>?, isSimpleBull25: Boolean): Boolean {
            return getScoreFromPointList(getPlayerRoundDarts(currentPlayer, currentRound, stackPoints), isSimpleBull25) == 180
        }

        /**
         * Tricks on a dart
         */
        fun isBulleye(point: Point) = point.value == BULL_VALUE

        /**
         * Tricks on a game
         */
        fun getFieldCount(fieldToCount: String, currentPlayer: Player, stackPoints: Stack<PlayerPoint>?): Long {
            var count = 0L
            val playerDarts = getPlayerDarts(currentPlayer, stackPoints)
            for (playerPoint in playerDarts) {
                count += if (fieldToCount == playerPoint.point.value) 1 else 0
            }
            return count
        }

        fun getDoubleCount(currentPlayer: Player, stackPoints: Stack<PlayerPoint>?): Long {
            var count = 0L
            val playerDarts = getPlayerDarts(currentPlayer, stackPoints)
            for (playerPoint in playerDarts) {
                count += if (playerPoint.point.isDoubled) 1 else 0
            }
            return count
        }

        fun getTripleCount(currentPlayer: Player, stackPoints: Stack<PlayerPoint>?): Long {
            var count = 0L
            val playerDarts = getPlayerDarts(currentPlayer, stackPoints)
            for (playerPoint in playerDarts) {
                count += if (playerPoint.point.isTripled) 1 else 0
            }
            return count
        }

        fun getTriple19Count(currentPlayer: Player, stackPoints: Stack<PlayerPoint>?): Long {
            var count = 0L
            val playerDarts = getPlayerDarts(currentPlayer, stackPoints)
            for (playerPoint in playerDarts) {
                count += if (playerPoint.point.isTripled && playerPoint.point.value == "19") 1 else 0
            }
            return count
        }

        fun getTriple20Count(currentPlayer: Player, stackPoints: Stack<PlayerPoint>?): Long {
            var count = 0L
            val playerDarts = getPlayerDarts(currentPlayer, stackPoints)
            for (playerPoint in playerDarts) {
                count += if (playerPoint.point.isTripled && playerPoint.point.value == "20") 1 else 0
            }
            return count
        }
    }
}
