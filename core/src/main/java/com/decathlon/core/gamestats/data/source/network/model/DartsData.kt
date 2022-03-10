package com.decathlon.core.gamestats.data.source.network.model

import com.google.gson.annotations.SerializedName

data class DartsData(
    // GAME STATS
    @SerializedName("98")
    val gamesPlayed: Int,
    @SerializedName("218")
    val gamesWon: Int,
    @SerializedName("219")
    val gamesWinPercent: Int,
    @SerializedName("220")
    val legsPlayed: Int,
    @SerializedName("221")
    val legsWon: Int,
    @SerializedName("222")
    val legsWinPercent: Int,
    @SerializedName("223")
    val ppd: Float,
    @SerializedName("224")
    val mpr: Float,
    @SerializedName("225")
    val dartsThrown: Long,

    // GAME TYPE
    @SerializedName("226")
    val game_01: Int,
    @SerializedName("227")
    val game_Cricket: Int,
    @SerializedName("228")
    val game_CountUp: Int,
    @SerializedName("229")
    val game_BullHunter: Int,
    @SerializedName("230")
    val game_Match: Int,
    @SerializedName("231")
    val game_HalfT: Int,
    @SerializedName("232")
    val game_Target: Int,
    @SerializedName("233")
    val game_Shangai: Int,
    @SerializedName("234")
    val game_AroundTheClock: Int,
    @SerializedName("235")
    val game_Over: Int,
    @SerializedName("236")
    val game_randomCheckout: Int,
    @SerializedName("237")
    val game_Landmine: Int,
    @SerializedName("238")
    val game_BaseBall: Int,
    @SerializedName("239")
    val game_3InLine: Int,
    @SerializedName("240")
    val game_SoccerPk: Int,
    @SerializedName("241")
    val game_BallTrap: Int,
    @SerializedName("242")
    val game_SuperBull: Int,

    // GAME PARAMS
    @SerializedName("243")
    val gameId: Long,
    @SerializedName("248")
    val playersNumber: Int,
    @SerializedName("244")
    val gameWinnerId: String,
    @SerializedName("245")
    val legsWinnerId: String,
    @SerializedName("246")
    val legsNumber: Int,
    @SerializedName("247")
    val roundNumber: Int,

    // PLAYER PARAMS
    @SerializedName("249")
    val playerId: Long,
    @SerializedName("250")
    val playerPPD: Float,
    @SerializedName("251")
    val playerMPR: Float,
    @SerializedName("252")
    val playerWin: Int,
    @SerializedName("253")
    val playerDartsThrown: Long,

    // TRICKS
    val babyTonTrick: Long,
    val bagONutsTrick: Long,
    val bullEyesTrick: Long,
    val bustTrick: Long,
    val hatTrick: Long,
    val highTownTrick: Long,
    val lowTownTrick: Long,
    val threeInABedTrick: Long,
    val tonTrick: Long,
    val ton80Trick: Long,
    val whiteHorseTrick: Long,
    val roundMore60Trick: Long,
    val roundMore100Trick: Long,
    val roundMore140Trick: Long,
    val round180Trick: Long,
    val dart15Trick: Long,
    val dart16Trick: Long,
    val dart17Trick: Long,
    val dart18Trick: Long,
    val dart19Trick: Long,
    val dart20Trick: Long,
    val dartBullTrick: Long,
    val roundHighest: Int,
    val averageScoreDart1: Int,
    val averageScoreDart2: Int,
    val averageScoreDart3: Int,
    val checkoutPercent: Float,
)
