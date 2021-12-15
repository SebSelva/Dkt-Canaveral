package com.decathlon.canaveral.game.countup

import com.decathlon.canaveral.Interactors
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.model.Point
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.canaveral.game.x01.Game01ViewModel

class CountUpViewModel(interactors: Interactors) : Game01ViewModel(interactors) {

    override fun addPlayerPoint(point: Point) {
        isPointBlinking = false
        isStackIncreasing = true
        isRoundDecreasing = false
        currentPlayer?.let {
            if (DartsUtils.getPlayerRoundDarts(it, currentRound, playersPoints).size < DartsUtils.DARTS_SHOTS_NUMBER) {
                currentRound = DartsUtils.getRoundNumber(players, playersPoints)
                playersPoints.push(PlayerPoint(it, point, currentRound, false))
                playersPointsLivedata.postValue(playersPoints)
            }
        }
    }
}