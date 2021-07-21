package com.decathlon.canaveral.game

import androidx.lifecycle.MutableLiveData
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.DartsUtils
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.model.Point
import java.util.*

class Game01ViewModel : BaseViewModel() {

    companion object {
        private const val DARTS_SHOTS_NUMBER = 3
        const val MAX_ROUNDS = 20
    }

    private var playersPoints: Stack<PlayerPoint> = Stack()
    private var currentPlayer: Player? = null

    var players: List<Player> = emptyList()
    var currentRound: Int = 1
    var isStackIncreasing: Boolean = true
    val currentPlayerLiveData: MutableLiveData<Player> = MutableLiveData()
    val playersPointsLivedata: MutableLiveData<Stack<PlayerPoint>> = MutableLiveData()

    fun addPlayerPoint(point: Point) {
        isStackIncreasing = true
        currentPlayer?.let {
            if (players.size == 1 || DartsUtils.getPlayerLastDarts(it, currentRound, playersPoints).size < DARTS_SHOTS_NUMBER) {
                currentRound = DartsUtils.getRoundNumber(players, playersPoints)
                playersPoints.push(PlayerPoint(it, point, currentRound))
                playersPointsLivedata.postValue(playersPoints)
            }
        }
    }

    fun removeLastPlayerPoint() {
        isStackIncreasing = false
        if (playersPoints.isNotEmpty()) {
            if (currentPlayer != playersPoints.peek().player || currentRound != playersPoints.peek().round) {
                currentPlayer = playersPoints.peek().player
                currentRound = playersPoints.peek().round
                currentPlayerLiveData.postValue(currentPlayer)
            } else {
                playersPoints.pop()
            }
        }
        playersPointsLivedata.postValue(playersPoints)
    }

    fun getCurrentPlayer() {
        if (currentPlayer == null) {
            selectNextPlayer()
        } else {
            currentPlayerLiveData.postValue(currentPlayer)
        }
    }

    fun selectNextPlayer() {
        currentPlayer = if (currentPlayer == null) {
            players.first()
        } else {
            players[(players.indexOf(currentPlayer)+1) % players.size]
        }
        currentRound = DartsUtils.getRoundNumber(players, playersPoints)
        currentPlayerLiveData.postValue(currentPlayer)
    }

    fun getPlayersPoints() {
        playersPointsLivedata.postValue(playersPoints)
    }

}
