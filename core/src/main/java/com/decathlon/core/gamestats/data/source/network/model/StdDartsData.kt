package com.decathlon.core.gamestats.data.source.network.model

import com.google.gson.annotations.SerializedName

data class StdDartsData(
    // GAME STATS
    @SerializedName("98")
    val gamesPlayed: Long,
    @SerializedName("218")
    val gamesWon: Long,
    @SerializedName("219")
    val gamesWinPercent: Long,
    @SerializedName("220")
    val legsPlayed: Long,
    @SerializedName("221")
    val legsWon: Long,
    @SerializedName("222")
    val legsWinPercent: Long,
    @SerializedName("323")
    val ppdTotalScored: Long,
    @SerializedName("322")
    val ppdDartsThrown: Long,
    @SerializedName("223")
    val ppd: Long,
    @SerializedName("224")
    val mpr: Long,
    @SerializedName("225")
    val dartsThrown: Long,

    // GAME TYPE
    @SerializedName("226")
    val game01: Long,
    @SerializedName("227")
    val gameCricket: Long,
    @SerializedName("228")
    val gameCountUp: Long,
    @SerializedName("229")
    val gameBullHunter: Long,
    @SerializedName("230")
    val gameMatch: Long,
    @SerializedName("231")
    val gameHalfT: Long,
    @SerializedName("232")
    val gameTarget: Long,
    @SerializedName("233")
    val gameShanghai: Long,
    @SerializedName("234")
    val gameAroundTheClock: Long,
    @SerializedName("235")
    val gameOver: Long,
    @SerializedName("236")
    val gamerandomCheckout: Long,
    @SerializedName("237")
    val gameLandmine: Long,
    @SerializedName("238")
    val gameBaseBall: Long,
    @SerializedName("239")
    val game3InLine: Long,
    @SerializedName("240")
    val gameSoccerPk: Long,
    @SerializedName("241")
    val gameBallTrap: Long,
    @SerializedName("242")
    val gameSuperBull: Long,

    // GAME PARAMS
    @SerializedName("243")
    val gameId: Long,
    @SerializedName("248")
    val playersNumber: Long,
    @SerializedName("244")
    val gameWinnerId: Long,
    @SerializedName("245")
    val legsWinnerId: Long,
    @SerializedName("246")
    val legsNumber: Long,
    @SerializedName("247")
    val roundNumber: Long,

    // PLAYER PARAMS
    @SerializedName("249")
    val playerId: Long,
    @SerializedName("250")
    val playerPPD: Long,
    @SerializedName("251")
    val playerMPR: Long,
    @SerializedName("252")
    val playerWin: Long,
    @SerializedName("253")
    val playerDartsThrownH: Long,
    @SerializedName("326")
    val playerDartsThrownL: Long,

    // TRICKS
    @SerializedName("254")
    val babyTonTrick: Long,
    @SerializedName("255")
    val bagONutsTrick: Long,
    @SerializedName("256")
    val bullEyesTrick: Long,
    @SerializedName("257")
    val bustTrick: Long,
    @SerializedName("258")
    val hatTrick: Long,
    @SerializedName("259")
    val highTownTrick: Long,
    @SerializedName("260")
    val lowTownTrick: Long,
    @SerializedName("261")
    val threeInABedTrick: Long,
    @SerializedName("263")
    val tonTrick: Long,
    @SerializedName("264")
    val ton80Trick: Long,
    @SerializedName("265")
    val whiteHorseTrick: Long,
    @SerializedName("266")
    val roundMore60Trick: Long,
    @SerializedName("267")
    val roundMore100Trick: Long,
    @SerializedName("268")
    val roundMore140Trick: Long,
    @SerializedName("269")
    val round180Trick: Long,
    @SerializedName("270")
    val dart15Trick: Long,
    @SerializedName("271")
    val dart16Trick: Long,
    @SerializedName("272")
    val dart17Trick: Long,
    @SerializedName("273")
    val dart18Trick: Long,
    @SerializedName("274")
    val dart19Trick: Long,
    @SerializedName("275")
    val dart20Trick: Long,
    @SerializedName("262")
    val dartBullTrick: Long,
    @SerializedName("276")
    val roundHighest: Long,
    @SerializedName("278")
    val averageScoreDart1: Long,
    @SerializedName("279")
    val averageScoreDart2: Long,
    @SerializedName("280")
    val averageScoreDart3: Long,
    @SerializedName("281")
    val checkoutPercent: Long,
    @SerializedName("282")
    val checkoutRecordH: Long,
    @SerializedName("283")
    val highScoreOn8Rounds: Long,
    val highScoreOn12Rounds: Long,
    val highScoreOn16Rounds: Long,
    val averageScoreRound: Long,

    // DART FIELDS STATS
    @SerializedName("284")
    val dartMissCount: Long,
    @SerializedName("285")
    val dart1Count: Long,
    @SerializedName("286")
    val dart2Count: Long,
    @SerializedName("287")
    val dart3Count: Long,
    @SerializedName("288")
    val dart4Count: Long,
    @SerializedName("289")
    val dart5Count: Long,
    @SerializedName("290")
    val dart6Count: Long,
    @SerializedName("291")
    val dart7Count: Long,
    @SerializedName("292")
    val dart8Count: Long,
    @SerializedName("293")
    val dart9Count: Long,
    @SerializedName("294")
    val dart10Count: Long,
    @SerializedName("295")
    val dart11Count: Long,
    @SerializedName("296")
    val dart12Count: Long,
    @SerializedName("297")
    val dart13Count: Long,
    @SerializedName("298")
    val dart14Count: Long,
    @SerializedName("299")
    val dart15Count: Long,
    @SerializedName("300")
    val dart16Count: Long,
    @SerializedName("301")
    val dart17Count: Long,
    @SerializedName("302")
    val dart18Count: Long,
    @SerializedName("303")
    val dart19Count: Long,
    @SerializedName("304")
    val dart20Count: Long,
    @SerializedName("305")
    val dartBullCount: Long,
    @SerializedName("306")
    val doubleCount: Long,
    @SerializedName("307")
    val tripleCount: Long,
    @SerializedName("309")
    val triple19Count: Long,
    @SerializedName("310")
    val triple20Count: Long,
)
