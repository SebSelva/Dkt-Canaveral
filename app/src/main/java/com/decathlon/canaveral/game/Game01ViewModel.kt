package com.decathlon.canaveral.game

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.DartsUtils
import com.decathlon.core.game.model.PlayerPoint
import com.decathlon.core.game.model.Point
import com.decathlon.core.player.model.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class Game01ViewModel : BaseViewModel() {

    companion object {
        private const val DARTS_SHOTS_NUMBER = 3
    }

    var players: List<Player> = emptyList()
    private var playersPoints: Stack<PlayerPoint> = Stack()

    private var currentPlayer: Player? = null

    var isStackIncreasing: Boolean = true
    val currentPlayerLiveData: MutableLiveData<Player> = MutableLiveData()
    val playersPointsLivedata: MutableLiveData<Stack<PlayerPoint>> = MutableLiveData()

    fun addPlayerPoint(point: Point) {
        isStackIncreasing = true
        currentPlayer?.let {
            if (DartsUtils.getPlayerLastDarts(it, playersPoints).size < DARTS_SHOTS_NUMBER) {
                playersPoints.push(PlayerPoint(it,point))
                playersPointsLivedata.postValue(playersPoints)
            }
        }
    }

    fun removeLastPlayerPoint() {
        isStackIncreasing = false
        if (playersPoints.isNotEmpty()) {
            if (currentPlayer != playersPoints.peek()?.player) {
                currentPlayer = playersPoints.peek()?.player
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
        currentPlayerLiveData.postValue(currentPlayer)
    }

    fun getPlayersPoints() {
        playersPointsLivedata.postValue(playersPoints)
    }

}
