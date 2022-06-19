package com.decathlon.canaveral.game.model

import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.core.gamestats.data.source.network.model.StdActivity
import com.decathlon.core.gamestats.data.source.network.model.StdDartsData
import java.util.*

abstract class DataStats(
    private val players: List<Player>,
    private val winners: List<Player>,
    private val currentPlayer: Player,
    private val stackPoints: Stack<PlayerPoint>?
) {
    abstract fun getStdActivity(): StdActivity

    fun get(): StdDartsData {
        val stdDataStats = StdDartsData()

        stdDataStats.gamesWon = if (winners.contains(currentPlayer)) 1 else 0
        stdDataStats.dartsThrown = DartsUtils.getPlayerDarts(currentPlayer, stackPoints).filter { !it.isBusted }.size.toLong()

        stdDataStats.playersNumber = players.size.toLong()
        stdDataStats.playerWin = if (winners.contains(currentPlayer)) 1 else 0

        stdDataStats.dartMissCount = DartsUtils.getFieldCount(DartsUtils.MISS_VALUE, currentPlayer, stackPoints)
        stdDataStats.dart1Count = DartsUtils.getFieldCount("1", currentPlayer, stackPoints)
        stdDataStats.dart2Count = DartsUtils.getFieldCount("2", currentPlayer, stackPoints)
        stdDataStats.dart3Count = DartsUtils.getFieldCount("3", currentPlayer, stackPoints)
        stdDataStats.dart4Count = DartsUtils.getFieldCount("4", currentPlayer, stackPoints)
        stdDataStats.dart5Count = DartsUtils.getFieldCount("5", currentPlayer, stackPoints)
        stdDataStats.dart6Count = DartsUtils.getFieldCount("6", currentPlayer, stackPoints)
        stdDataStats.dart7Count = DartsUtils.getFieldCount("7", currentPlayer, stackPoints)
        stdDataStats.dart8Count = DartsUtils.getFieldCount("8", currentPlayer, stackPoints)
        stdDataStats.dart9Count = DartsUtils.getFieldCount("9", currentPlayer, stackPoints)
        stdDataStats.dart10Count = DartsUtils.getFieldCount("10", currentPlayer, stackPoints)
        stdDataStats.dart11Count = DartsUtils.getFieldCount("11", currentPlayer, stackPoints)
        stdDataStats.dart12Count = DartsUtils.getFieldCount("12", currentPlayer, stackPoints)
        stdDataStats.dart13Count = DartsUtils.getFieldCount("13", currentPlayer, stackPoints)
        stdDataStats.dart14Count = DartsUtils.getFieldCount("14", currentPlayer, stackPoints)
        stdDataStats.dart15Count = DartsUtils.getFieldCount("15", currentPlayer, stackPoints)
        stdDataStats.dart16Count = DartsUtils.getFieldCount("16", currentPlayer, stackPoints)
        stdDataStats.dart17Count = DartsUtils.getFieldCount("17", currentPlayer, stackPoints)
        stdDataStats.dart18Count = DartsUtils.getFieldCount("18", currentPlayer, stackPoints)
        stdDataStats.dart19Count = DartsUtils.getFieldCount("19", currentPlayer, stackPoints)
        stdDataStats.dart20Count = DartsUtils.getFieldCount("20", currentPlayer, stackPoints)
        stdDataStats.dartBullCount = DartsUtils.getFieldCount(DartsUtils.BULL_VALUE, currentPlayer, stackPoints)
        stdDataStats.doubleCount = DartsUtils.getDoubleCount(currentPlayer, stackPoints)
        stdDataStats.tripleCount = DartsUtils.getTripleCount(currentPlayer, stackPoints)
        stdDataStats.triple19Count = DartsUtils.getTriple19Count(currentPlayer, stackPoints)
        stdDataStats.triple20Count = DartsUtils.getTriple20Count(currentPlayer, stackPoints)

        return stdDataStats
    }
}