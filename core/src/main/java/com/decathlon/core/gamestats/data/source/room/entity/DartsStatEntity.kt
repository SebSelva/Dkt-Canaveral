package com.decathlon.core.gamestats.data.source.room.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.decathlon.core.gamestats.data.source.network.model.StdStat
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@Parcelize
@Entity(tableName = "dartStats")
data class DartsStatEntity (
    @PrimaryKey
    var playerId: Long,
    var duration: Long,

    // GAME STATS
    var gamesPlayed: Long,
    var gamesWon: Long,
    var gamesWinPercent: Long,
    var gamesDraw: Long,
    var gamesDrawPercent: Long,
    var legsPlayed: Long,
    var legsWon: Long,
    var legsWinPercent: Long,
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
    var dart15Trick: Long,
    var dart16Trick: Long,
    var dart17Trick: Long,
    var dart18Trick: Long,
    var dart19Trick: Long,
    var dart20Trick: Long,
    var dartBullTrick: Long,
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
    var dart15Count: Long,
    var dart16Count: Long,
    var dart17Count: Long,
    var dart18Count: Long,
    var dart19Count: Long,
    var dart20Count: Long,
    var dartBullCount: Long,
    var doubleCount: Long,
    var tripleCount: Long,
    var triple19Count: Long,
    var triple20Count: Long

): Parcelable {

    constructor() : this(0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)

    fun setFromStdStatList(list: List<StdStat>) {
        for (stat in list) {
            setValueFromDatatype(stat.datatype, stat.value)
        }
    }
    
    private fun setValueFromDatatype(dataType: String, value: Long) {
        when(val id = dataType.substringAfterLast("/").toInt()) {
            in 24..225, 319 -> setGeneralStat(id, value)
            in 226..242 -> setGameTypeStat(id, value)
            in 243..248 -> setGameInfoStat(id, value)
            in 249..253, 326 -> setPlayerInfoStat(id, value)
            in 254..283 -> setTricksStat(id, value)
            in 284..310 -> setAreaCountStat(id, value)
            else -> Timber.w("ID $id not managed")
        }
    }

    private fun setGeneralStat(index: Int, value: Long) {
        when (index) {
            24 -> duration = value
            98 -> gamesPlayed = value
            99 -> {/*Nothing with this*/}
            218 -> gamesWon = value
            219 -> gamesWinPercent = value
            319 -> gamesDraw = value
            220 -> legsPlayed = value
            221 -> legsWon = value
            222 -> legsWinPercent = value
            323 -> ppdTotalScored = value
            322 -> ppdDartsThrown = value
            223 -> ppd = value
            224 -> mpr = value
            225 -> dartsThrown = value
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

    fun setGameInfoStat(index: Int, value: Long) {
        when (index) {
            243 -> gameId = value
            248 -> playersNumber = value
            244 -> gameWinnerId = value
            245 -> legsWinnerId = value
            246 -> legsNumber = value
            247 -> roundNumber = value
        }
    }

    fun setPlayerInfoStat(index: Int, value: Long) {
        when (index) {
            249 -> playerId = value
            250 -> playerPPD = value
            251 -> playerMPR = value
            252 -> playerWin = value
            253 -> playerDartsThrownH = value
            326 -> playerDartsThrownL = value
        }
    }

    fun setTricksStat(index: Int, value: Long) {
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
            270 -> dart15Trick = value
            271 -> dart16Trick = value
            272 -> dart17Trick = value
            273 -> dart18Trick = value
            274 -> dart19Trick = value
            275 -> dart20Trick = value
            262 -> dartBullTrick = value
            276 -> roundHighest = value
            278 -> averageScoreDart1 = value
            279 -> averageScoreDart2 = value
            280 -> averageScoreDart3 = value
            281 -> checkoutPercent = value
            282 -> checkoutRecordH = value
            283 -> highScoreOn8Rounds = value
        }
    }

    private fun setAreaCountStat(index: Int, value: Long) {
        when (index) {
            284 -> dartMissCount = value
            285 -> dart1Count = value
            286 -> dart2Count = value
            287 -> dart3Count = value
            288 -> dart4Count = value
            289 -> dart5Count = value
            290 -> dart6Count = value
            291 -> dart7Count = value
            292 -> dart8Count = value
            293 -> dart9Count = value
            294 -> dart10Count = value
            295 -> dart11Count = value
            296 -> dart12Count = value
            297 -> dart13Count = value
            298 -> dart14Count = value
            299 -> dart15Count = value
            300 -> dart16Count = value
            301 -> dart17Count = value
            302 -> dart18Count = value
            303 -> dart19Count = value
            304 -> dart20Count = value
            305 -> dartBullCount = value
            306 -> doubleCount = value
            307 -> tripleCount = value
            309 -> triple19Count = value
            310 -> triple20Count = value
        }
    }
}