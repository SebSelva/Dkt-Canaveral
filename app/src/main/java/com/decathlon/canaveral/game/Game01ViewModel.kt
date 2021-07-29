package com.decathlon.canaveral.game

import androidx.lifecycle.MutableLiveData
import com.decathlon.canaveral.Interactors
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.model.Point
import com.decathlon.canaveral.common.utils.DartsUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class Game01ViewModel(private val interactors: Interactors) : BaseViewModel() {

    companion object {
        private const val DARTS_SHOTS_NUMBER = 3
    }

    var playersPoints: Stack<PlayerPoint> = Stack()
    var currentPlayer: Player? = null
    var players: List<Player> = emptyList()
    var currentRound: Int = 1
    var isStackIncreasing: Boolean = true
    var isRoundDecreasing: Boolean = false

    val currentPlayerLiveData: MutableLiveData<Player> = MutableLiveData()
    val playersPointsLivedata: MutableLiveData<Stack<PlayerPoint>> = MutableLiveData()

    private suspend fun getPlayers() {
        players = interactors.getPlayers().first().map { player -> Player(player) }
        getCurrentPlayer()
    }

    fun addPlayerPoint(point: Point) {
        isStackIncreasing = true
        isRoundDecreasing = false
        currentPlayer?.let {
            if (players.size == 1 || DartsUtils.getPlayerRoundDarts(it, currentRound, playersPoints).size < DARTS_SHOTS_NUMBER) {
                currentRound = DartsUtils.getRoundNumber(players, playersPoints)
                playersPoints.push(PlayerPoint(it, point, currentRound))
                playersPointsLivedata.postValue(playersPoints)
            }
        }
    }

    fun removeLastPlayerPoint() {
        isStackIncreasing = false
        isRoundDecreasing = false
        if (playersPoints.isNotEmpty()) {
            if (currentPlayer != playersPoints.peek().player || currentRound != playersPoints.peek().round) {
                currentPlayer = playersPoints.peek().player
                currentRound = playersPoints.peek().round
                isRoundDecreasing = true
                currentPlayerLiveData.postValue(currentPlayer)
            } else {
                playersPoints.pop()
            }
        }
        playersPointsLivedata.postValue(playersPoints)
    }

    fun getCurrentPlayer() {
        when {
            players.isEmpty() -> {
                runBlocking {
                    launch { getPlayers() }
                }
            }
            currentPlayer == null -> {
                selectNextPlayer()
            }
            else -> {
                currentPlayerLiveData.postValue(currentPlayer)
            }
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
