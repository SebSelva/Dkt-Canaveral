package com.decathlon.canaveral.game

import androidx.lifecycle.MutableLiveData
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.core.game.model.PlayerPoint
import com.decathlon.core.game.model.Point
import com.decathlon.core.player.model.Player
import java.util.*

class Game01ViewModel : BaseViewModel() {

    var variant: Int? = null

    private var players: List<Player> = emptyList()
    private var playersPoints: Stack<PlayerPoint>? = Stack()

    private var currentPlayer: Player? = null

    val currentPlayerLiveData: MutableLiveData<Player> = MutableLiveData()
    val playersPointsLivedata: MutableLiveData<Stack<PlayerPoint>> = MutableLiveData()

    fun setPlayers(playersList: List<Player>) {
        players = playersList
    }

    fun addPlayerPoint(point: Point) {
        playersPoints?.push(currentPlayer?.let { PlayerPoint(it,point) })
        playersPointsLivedata.postValue(playersPoints)
    }

    fun removeLastPlayerPoint() {
        if (playersPoints?.isNotEmpty() == true) {
            playersPoints?.pop()
            if (playersPoints?.isNotEmpty() == true && currentPlayer != playersPoints?.peek()?.player) {
                currentPlayer = playersPoints?.peek()?.player
                currentPlayerLiveData.postValue(currentPlayer)
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
