package com.decathlon.canaveral.game.countup

import androidx.lifecycle.viewModelScope
import com.decathlon.canaveral.common.interactors.Interactors
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.model.PlayerStats
import com.decathlon.canaveral.common.model.Point
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.canaveral.game.model.CountUpDataStats
import com.decathlon.canaveral.game.model.Game01DataStats
import com.decathlon.canaveral.game.x01.Game01ViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class CountUpViewModel(val interactors: Interactors) : Game01ViewModel(interactors) {

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

    override fun updateActivity(winPlayers: MutableList<PlayerStats>) {
        currentPlayer?.let { player ->
            val dataStats = CountUpDataStats(
                currentRound.toLong(),
                players,
                winPlayers.map { player },
                player,
                playersPoints,
                isBull25
            )
            val activity = dataStats.getStdActivity()
            activity.user?.let {
                viewModelScope.launch {
                    interactors.stdActions.saveActivity(activity)
                    getAccessToken(interactors)?.let { token ->
                        interactors.stdActions.postUserActivity(token, activity)
                    }
                }
            } ?: Timber.i("Update activity player")
        }
    }

}