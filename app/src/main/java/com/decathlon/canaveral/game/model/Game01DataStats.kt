package com.decathlon.canaveral.game.model

import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.core.common.utils.getDateTimeFormatted
import com.decathlon.core.gamestats.data.source.network.model.StdActivity
import java.util.*
import kotlin.math.max

class Game01DataStats(
    private val gameLength: Long,
    players: List<Player>,
    private val winners: List<Player>,
    private val currentPlayer: Player,
    private val stackPoints: Stack<PlayerPoint>?,
    private val isSimpleBull25: Boolean
): DataStats(players, winners, currentPlayer, stackPoints) {

    override fun getStdActivity(): StdActivity {
        val stdDataStats = get()

        val nbRounds = DartsUtils.getPlayerDarts(currentPlayer, stackPoints).last().round

        // ROUND
        for (round in 0..nbRounds) {
            stdDataStats.babyTonTrick += if (DartsUtils.isBabyTon(currentPlayer, round, stackPoints, isSimpleBull25)) 1 else 0
            stdDataStats.bagONutsTrick += if (DartsUtils.isBagONuts(currentPlayer, round, stackPoints, isSimpleBull25)) 1 else 0
            for (point in DartsUtils.getPlayerRoundDarts(currentPlayer,round,stackPoints)) {
                stdDataStats.bullEyesTrick += if (DartsUtils.isBulleye(point)) 1 else 0
            }
            stdDataStats.bustTrick += if (DartsUtils.getPlayerPointRoundDarts(currentPlayer,round,stackPoints)?.last()?.isBusted == true) 1 else 0
            stdDataStats.hatTrick += if (DartsUtils.isHatTrick(currentPlayer, round, stackPoints)) 1 else 0
            stdDataStats.highTownTrick += if (DartsUtils.isHighTon(currentPlayer, round, stackPoints, isSimpleBull25)) 1 else 0
            stdDataStats.lowTownTrick += if (DartsUtils.isLowTon(currentPlayer, round, stackPoints, isSimpleBull25)) 1 else 0
            stdDataStats.threeInABedTrick += if (DartsUtils.isThreeInABed(currentPlayer, round, stackPoints)) 1 else 0
            stdDataStats.tonTrick += if (DartsUtils.isTon(currentPlayer, round, stackPoints, isSimpleBull25)) 1 else 0
            stdDataStats.ton80Trick += if (DartsUtils.isTon80(currentPlayer, round, stackPoints, isSimpleBull25)) 1 else 0
            stdDataStats.roundMore60Trick += if (DartsUtils.isMoreThan60(currentPlayer, round, stackPoints, isSimpleBull25)) 1 else 0
            stdDataStats.roundMore100Trick += if (DartsUtils.isMoreThan100(currentPlayer, round, stackPoints, isSimpleBull25)) 1 else 0
            stdDataStats.roundMore140Trick += if (DartsUtils.isMoreThan140(currentPlayer, round, stackPoints, isSimpleBull25)) 1 else 0
            stdDataStats.round180Trick += if (DartsUtils.is180(currentPlayer, round, stackPoints, isSimpleBull25)) 1 else 0
            stdDataStats.roundHighest = max(stdDataStats.roundHighest,
                DartsUtils.getScoreFromPointList(DartsUtils.getPlayerRoundDarts(currentPlayer,round,stackPoints), isSimpleBull25).toLong())
        }

        // GAME 01
        //TODO: Verify that all stats for this game is filled
        stdDataStats.game01 = 1
        stdDataStats.game01Won = if (winners.contains(currentPlayer)) 1 else 0
        stdDataStats.ppdTotalScored = DartsUtils.getScoreFromPointList(DartsUtils.getPlayerDarts(currentPlayer, stackPoints).filter { !it.isBusted }.map { it.point }, isSimpleBull25).toLong()
        stdDataStats.ppdDartsThrown = DartsUtils.getPlayerDarts(currentPlayer, stackPoints).filter { !it.isBusted }.size.toLong()

        return StdActivity(
            currentPlayer.nickname,
            null,
            "/v2/sports/316",
            getDateTimeFormatted(),
            gameLength,
            "/v2/connectors/806",
            stdDataStats
        )
    }
}