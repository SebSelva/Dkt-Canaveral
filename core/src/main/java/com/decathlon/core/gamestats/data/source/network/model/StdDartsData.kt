package com.decathlon.core.gamestats.data.source.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@Parcelize
data class StdDartsData(
    // GAME STATS
    @SerializedName("218")
    var gamesWon: Long,
    @SerializedName("220")
    var legsPlayed: Long,
    @SerializedName("221")
    var legsWon: Long,
    @SerializedName("323")
    var ppdTotalScored: Long,
    @SerializedName("322")
    var ppdDartsThrown: Long,
    @SerializedName("223")
    var ppd: Long,
    @SerializedName("224")
    var mpr: Long,
    @SerializedName("225")
    var dartsThrown: Long,

    // GAME TYPE
    @SerializedName("226")
    var game01: Long,
    @SerializedName("227")
    var gameCricket: Long,
    @SerializedName("228")
    var gameCountUp: Long,
    @SerializedName("229")
    var gameBullHunter: Long,
    @SerializedName("230")
    var gameMatch: Long,
    @SerializedName("231")
    var gameHalfT: Long,
    @SerializedName("232")
    var gameTarget: Long,
    @SerializedName("233")
    var gameShanghai: Long,
    @SerializedName("234")
    var gameAroundTheClock: Long,
    @SerializedName("235")
    var gameOver: Long,
    @SerializedName("236")
    var gameRandomCheckout: Long,
    @SerializedName("237")
    var gameLandmine: Long,
    @SerializedName("238")
    var gameBaseBall: Long,
    @SerializedName("239")
    var game3InLine: Long,
    @SerializedName("240")
    var gameSoccerPk: Long,
    @SerializedName("241")
    var gameBallTrap: Long,
    @SerializedName("242")
    var gameSuperBull: Long,

    @SerializedName("349")
    var game01Won: Long,
    @SerializedName("351")
    var gameCricketWon: Long,
    @SerializedName("350")
    var gameCountUpWon: Long,
    @SerializedName("352")
    var gameBullHunterWon: Long,
    @SerializedName("353")
    var gameMatchWon: Long,
    @SerializedName("354")
    var gameHalfTWon: Long,
    @SerializedName("355")
    var gameTargetWon: Long,
    @SerializedName("356")
    var gameShanghaiWon: Long,
    @SerializedName("357")
    var gameAroundTheClockWon: Long,
    @SerializedName("358")
    var gameOverWon: Long,
    @SerializedName("359")
    var gameRandomCheckoutWon: Long,
    @SerializedName("360")
    var gameLandmineWon: Long,
    @SerializedName("361")
    var gameBaseBallWon: Long,
    @SerializedName("362")
    var game3InLineWon: Long,
    @SerializedName("363")
    var gameSoccerPkWon: Long,
    @SerializedName("364")
    var gameBallTrapWon: Long,
    @SerializedName("365")
    var gameSuperBullWon: Long,

    // GAME PARAMS
    @SerializedName("243")
    var gameId: Long,
    @SerializedName("248")
    var playersNumber: Long,
    @SerializedName("244")
    var gameWinnerId: Long,
    @SerializedName("245")
    var legsWinnerId: Long,
    @SerializedName("246")
    var legsNumber: Long,
    @SerializedName("247")
    var roundNumber: Long,

    // PLAYER PARAMS
    @SerializedName("249")
    var playerId: Long,
    @SerializedName("250")
    var playerPPD: Long,
    @SerializedName("251")
    var playerMPR: Long,
    @SerializedName("252")
    var playerWin: Long,
    @SerializedName("253")
    var playerDartsThrownH: Long,
    @SerializedName("326")
    var playerDartsThrownL: Long,

    // TRICKS
    @SerializedName("254")
    var babyTonTrick: Long,
    @SerializedName("255")
    var bagONutsTrick: Long,
    @SerializedName("256")
    var bullEyesTrick: Long,
    @SerializedName("257")
    var bustTrick: Long,
    @SerializedName("258")
    var hatTrick: Long,
    @SerializedName("259")
    var highTownTrick: Long,
    @SerializedName("260")
    var lowTownTrick: Long,
    @SerializedName("261")
    var threeInABedTrick: Long,
    @SerializedName("263")
    var tonTrick: Long,
    @SerializedName("264")
    var ton80Trick: Long,
    @SerializedName("265")
    var whiteHorseTrick: Long,
    @SerializedName("266")
    var roundMore60Trick: Long,
    @SerializedName("267")
    var roundMore100Trick: Long,
    @SerializedName("268")
    var roundMore140Trick: Long,
    @SerializedName("269")
    var round180Trick: Long,
    @SerializedName("270")
    var dart15Count: Long,
    @SerializedName("271")
    var dart16Count: Long,
    @SerializedName("272")
    var dart17Count: Long,
    @SerializedName("273")
    var dart18Count: Long,
    @SerializedName("274")
    var dart19Count: Long,
    @SerializedName("275")
    var dart20Count: Long,
    @SerializedName("262")
    var dartBullCount: Long,
    @SerializedName("276")
    var roundHighest: Long,
    @SerializedName("278")
    var averageScoreDart1: Long,
    @SerializedName("279")
    var averageScoreDart2: Long,
    @SerializedName("280")
    var averageScoreDart3: Long,
    @SerializedName("281")
    var checkoutPercent: Long,
    @SerializedName("282")
    var checkoutRecordH: Long,
    @SerializedName("283")
    var highScoreOn8Rounds: Long,
    @SerializedName("347")
    var highScoreOn12Rounds: Long,
    @SerializedName("348")
    var highScoreOn16Rounds: Long,

    // DART FIELDS STATS
    @SerializedName("327")
    var dartMissCount: Long,
    @SerializedName("328")
    var dart1Count: Long,
    @SerializedName("329")
    var dart2Count: Long,
    @SerializedName("330")
    var dart3Count: Long,
    @SerializedName("331")
    var dart4Count: Long,
    @SerializedName("332")
    var dart5Count: Long,
    @SerializedName("333")
    var dart6Count: Long,
    @SerializedName("334")
    var dart7Count: Long,
    @SerializedName("335")
    var dart8Count: Long,
    @SerializedName("336")
    var dart9Count: Long,
    @SerializedName("337")
    var dart10Count: Long,
    @SerializedName("338")
    var dart11Count: Long,
    @SerializedName("339")
    var dart12Count: Long,
    @SerializedName("340")
    var dart13Count: Long,
    @SerializedName("341")
    var dart14Count: Long,
    @SerializedName("342")
    var doubleCount: Long,
    @SerializedName("343")
    var tripleCount: Long,
    @SerializedName("344")
    var triple19Count: Long,
    @SerializedName("345")
    var triple20Count: Long,
) : Parcelable {
    constructor() : this(
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    )

}

fun StdDartsData.getDartCount(index: Int): Long {
    return when (index) {
        327 -> dartMissCount
        328 -> dart1Count
        329 -> dart2Count
        330 -> dart3Count
        331 -> dart4Count
        332 -> dart5Count
        333 -> dart6Count
        334 -> dart7Count
        335 -> dart8Count
        336 -> dart9Count
        337 -> dart10Count
        338 -> dart11Count
        339 -> dart12Count
        340 -> dart13Count
        341 -> dart14Count
        270 -> dart15Count
        271 -> dart16Count
        272 -> dart17Count
        273 -> dart18Count
        274 -> dart19Count
        275 -> dart20Count
        262 -> dartBullCount
        342 -> doubleCount
        343 -> tripleCount
        344 -> triple19Count
        345 -> triple20Count
        else -> {
            Timber.w("DartCount $index doesn't exist")
            -1L
        }
    }
}