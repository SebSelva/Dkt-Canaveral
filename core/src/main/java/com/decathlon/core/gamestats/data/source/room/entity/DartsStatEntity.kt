package com.decathlon.core.gamestats.data.source.room.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.decathlon.core.gamestats.data.source.network.model.StdStat
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import java.util.*

@Parcelize
@Entity(tableName = "dartStats")
data class DartsStatEntity (
    @PrimaryKey
    var playerId: Long,
    var dateTime: Long,
    var duration: Long,

    // GAME STATS
    var gamesPlayed: Long,
    var gamesWon: Long,
    var gamesWinPercent: Float,
    var gamesDraw: Long,
    var gamesDrawPercent: Float,
    var legsPlayed: Long,
    var legsWon: Long,
    var legsWinPercent: Float,
    var ppdTotalScored: Long,
    var ppdDartsThrown: Long,
    var ppd: Long,
    var mpr: Long,
    var dartsThrown: Long,

    // GAME TYPE
    var game01: Long,
    var gameCricket: Long,
    var gameCountup: Long,
    var gameBullhunter: Long,
    var gameMatch: Long,
    var gameHalft: Long,
    var gameTarget: Long,
    var gameShanghai: Long,
    var gameAroundtheclock: Long,
    var gameOver: Long,
    var gameRandomcheckout: Long,
    var gameLandmine: Long,
    var gameBaseball: Long,
    var game3inline: Long,
    var gameSoccerpk: Long,
    var gameBalltrap: Long,
    var gameSuperbull: Long,

    // GAME WIN BY TYPE
    var game01Won: Long,
    var gameCricketWon: Long,
    var gameCountupWon: Long,
    var gameBullhunterWon: Long,
    var gameMatchWon: Long,
    var gameHalftWon: Long,
    var gameTargetWon: Long,
    var gameShanghaiWon: Long,
    var gameAroundtheclockWon: Long,
    var gameOverWon: Long,
    var gameRandomcheckoutWon: Long,
    var gameLandmineWon: Long,
    var gameBaseballWon: Long,
    var game3inlineWon: Long,
    var gameSoccerpkWon: Long,
    var gameBalltrapWon: Long,
    var gameSuperbullWon: Long,

    // GAME PARAMS
    var gameId: Long,
    var playersNumber: Long,
    var gameWinnerId: Long,
    var legsWinnerId: Long,
    var legsNumber: Long,
    var roundNumber: Long,

    // PLAYER PARAMS
    var playerPPD: Long,
    var playerMPR: Long,
    var playerWin: Long,
    var playerDartsThrownH: Long,
    var playerDartsThrownL: Long,

    // TRICKS
    var babyTonTrick: Long,
    var bagONutsTrick: Long,
    var bullEyesTrick: Long,
    var bustTrick: Long,
    var hatTrick: Long,
    var highTownTrick: Long,
    var lowTownTrick: Long,
    var threeInABedTrick: Long,
    var tonTrick: Long,
    var ton80Trick: Long,
    var whiteHorseTrick: Long,
    var roundMore60Trick: Long,
    var roundMore100Trick: Long,
    var roundMore140Trick: Long,
    var round180Trick: Long,
    var dart15Count: Long,
    var dart16Count: Long,
    var dart17Count: Long,
    var dart18Count: Long,
    var dart19Count: Long,
    var dart20Count: Long,
    var dartBullCount: Long,
    var roundHighest: Long,
    var averageScoreDart1: Long,
    var averageScoreDart2: Long,
    var averageScoreDart3: Long,
    var checkoutPercent: Long,
    var checkoutRecordH: Long,
    var highScoreOn8Rounds: Long,
    var highScoreOn12Rounds: Long,
    var highScoreOn16Rounds: Long,
    var averageScoreRound: Long,

    // DART FIELDS STATS
    var dartMissPercent: Float,
    var dart1Percent: Float,
    var dart2Percent: Float,
    var dart3Percent: Float,
    var dart4Percent: Float,
    var dart5Percent: Float,
    var dart6Percent: Float,
    var dart7Percent: Float,
    var dart8Percent: Float,
    var dart9Percent: Float,
    var dart10Percent: Float,
    var dart11Percent: Float,
    var dart12Percent: Float,
    var dart13Percent: Float,
    var dart14Percent: Float,
    var dart15Percent: Float,
    var dart16Percent: Float,
    var dart17Percent: Float,
    var dart18Percent: Float,
    var dart19Percent: Float,
    var dart20Percent: Float,
    var dartBullPercent: Float,
    var doublePercent: Float,
    var triplePercent: Float,
    var triple19Percent: Float,
    var triple20Percent: Float,

    var dartMissCount: Long,
    var dart1Count: Long,
    var dart2Count: Long,
    var dart3Count: Long,
    var dart4Count: Long,
    var dart5Count: Long,
    var dart6Count: Long,
    var dart7Count: Long,
    var dart8Count: Long,
    var dart9Count: Long,
    var dart10Count: Long,
    var dart11Count: Long,
    var dart12Count: Long,
    var dart13Count: Long,
    var dart14Count: Long,
    var doubleCount: Long,
    var tripleCount: Long,
    var triple19Count: Long,
    var triple20Count: Long

): Parcelable {

    constructor() : this(0,0,0,0,0,0F,0,0F,0,0,0F,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,0F,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)

    fun setFromStatList(list: List<StdStat>) {
        for (stat in list) {
            setValueFromDatatype(stat.datatype, stat.value)
        }
    }

    fun setDateTime() {
        dateTime = Calendar.getInstance().time.time
    }
    
    fun setValueFromDatatype(dataType: String, value: Any) {
        when(val id = dataType.substringAfterLast("/").toInt()) {
            in 24..225, 319, 322, 323 -> setGeneralStat(id, value)
            in 226..242 -> setGameTypeStat(id, value as Long)
            in 243..248 -> setGameInfoStat(id, value as Long)
            in 249..253, 326 -> setPlayerInfoStat(id, value as Long)
            in 254..283, 347, 348 -> setTricksStat(id, value as Long)
            in 284..310 -> setAreaPercentStat(id, value as Float)
            in 327..352 -> setAreaCountStat(id, value as Long)
            in 349..365 -> setGameWonByType(id, value as Long)
            else -> Timber.w("ID $id not managed")
        }
    }

    private fun setGameWonByType(index: Int, value: Long) {
        when (index) {
            349 -> game01Won = value
            350 -> gameCricketWon = value
            351 -> gameCountupWon = value
            352 -> gameBullhunterWon = value
            353 -> gameMatchWon = value
            354 -> gameHalftWon = value
            355 -> gameTargetWon = value
            356 -> gameShanghaiWon = value
            357 -> gameAroundtheclockWon = value
            358 -> gameOverWon = value
            359 -> gameRandomcheckoutWon = value
            360 -> gameLandmineWon = value
            361 -> gameBaseballWon = value
            362 -> game3inlineWon = value
            363 -> gameSoccerpkWon = value
            364 -> gameBalltrapWon = value
            365 -> gameSuperbullWon = value
        }
    }

    private fun setGeneralStat(index: Int, value: Any) {
        when (index) {
            24 -> duration = value as Long
            98 -> gamesPlayed = value as Long
            99 -> {/*Nothing with this*/}
            218 -> gamesWon = value as Long
            219 -> gamesWinPercent = value as Float
            319 -> gamesDraw = value as Long
            // Add to STD gamesDrawPercent
            220 -> legsPlayed = value as Long
            221 -> legsWon = value as Long
            222 -> legsWinPercent = value as Float
            323 -> ppdTotalScored = value as Long
            322 -> ppdDartsThrown = value as Long
            223 -> ppd = value as Long
            224 -> mpr = value as Long
            225 -> dartsThrown = value as Long
        }
    }

    private fun setGameTypeStat(index: Int, value: Long) {
        when (index) {
            226 -> game01 = value
            227 -> gameCricket = value
            228 -> gameCountup = value
            229 -> gameBullhunter = value
            230 -> gameMatch = value
            231 -> gameHalft = value
            232 -> gameTarget = value
            233 -> gameShanghai = value
            234 -> gameAroundtheclock = value
            235 -> gameOver = value
            236 -> gameRandomcheckout = value
            237 -> gameLandmine = value
            238 -> gameBaseball = value
            239 -> game3inline = value
            240 -> gameSoccerpk = value
            241 -> gameBalltrap = value
            242 -> gameSuperbull = value
        }
    }

    private fun setGameInfoStat(index: Int, value: Long) {
        when (index) {
            243 -> gameId = value
            248 -> playersNumber = value
            244 -> gameWinnerId = value
            245 -> legsWinnerId = value
            246 -> legsNumber = value
            247 -> roundNumber = value
        }
    }

    private fun setPlayerInfoStat(index: Int, value: Long) {
        when (index) {
            249 -> playerId = value
            250 -> playerPPD = value
            251 -> playerMPR = value
            252 -> playerWin = value
            253 -> playerDartsThrownH = value
            326 -> playerDartsThrownL = value
        }
    }

    private fun setTricksStat(index: Int, value: Long) {
        when (index) {
            254 -> babyTonTrick = value
            255 -> bagONutsTrick = value
            256 -> bullEyesTrick = value
            257 -> bustTrick = value
            258 -> hatTrick = value
            259 -> highTownTrick = value
            260 -> lowTownTrick = value
            261 -> threeInABedTrick = value
            263 -> tonTrick = value
            264 -> ton80Trick = value
            265 -> whiteHorseTrick = value
            266 -> roundMore60Trick = value
            267 -> roundMore100Trick = value
            268 -> roundMore140Trick = value
            269 -> round180Trick = value
            270 -> dart15Count = value
            271 -> dart16Count = value
            272 -> dart17Count = value
            273 -> dart18Count = value
            274 -> dart19Count = value
            275 -> dart20Count = value
            262 -> dartBullCount = value
            276 -> roundHighest = value
            278 -> averageScoreDart1 = value
            279 -> averageScoreDart2 = value
            280 -> averageScoreDart3 = value
            281 -> checkoutPercent = value
            282 -> checkoutRecordH = value
            283 -> highScoreOn8Rounds = value
            347 -> highScoreOn12Rounds = value
            348 -> highScoreOn16Rounds = value
        }
    }

    private fun setAreaPercentStat(index: Int, value: Float) {
        when (index) {
            284 -> dartMissPercent = value
            285 -> dart1Percent = value
            286 -> dart2Percent = value
            287 -> dart3Percent = value
            288 -> dart4Percent = value
            289 -> dart5Percent = value
            290 -> dart6Percent = value
            291 -> dart7Percent = value
            292 -> dart8Percent = value
            293 -> dart9Percent = value
            294 -> dart10Percent = value
            295 -> dart11Percent = value
            296 -> dart12Percent = value
            297 -> dart13Percent = value
            298 -> dart14Percent = value
            299 -> dart15Percent = value
            300 -> dart16Percent = value
            301 -> dart17Percent = value
            302 -> dart18Percent = value
            303 -> dart19Percent = value
            304 -> dart20Percent = value
            305 -> dartBullPercent = value
            306 -> doublePercent = value
            307 -> triplePercent = value
            309 -> triple19Percent = value
            310 -> triple20Percent = value
        }
    }

    private fun setAreaCountStat(index: Int, value: Long) {
        when (index) {
            327 -> dartMissCount = value
            328 -> dart1Count = value
            329 -> dart2Count = value
            330 -> dart3Count = value
            331 -> dart4Count = value
            332 -> dart5Count = value
            333 -> dart6Count = value
            334 -> dart7Count = value
            335 -> dart8Count = value
            336 -> dart9Count = value
            337 -> dart10Count = value
            338 -> dart11Count = value
            339 -> dart12Count = value
            340 -> dart13Count = value
            341 -> dart14Count = value
            342 -> doubleCount = value
            343 -> tripleCount = value
            344 -> triple19Count = value
            345 -> triple20Count = value
        }
    }
}