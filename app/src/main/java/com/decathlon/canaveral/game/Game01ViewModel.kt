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
    private var playersPoints: Stack<PlayerPoint>? = null

    private var currentPlayer: Player? = null

    val playersPointsLivedata: MutableLiveData<Stack<PlayerPoint>?> = MutableLiveData()

    fun setPlayers(playersList: List<Player>) {
        players = playersList
    }

    fun addPlayerPoint(point: Point) {
        playersPoints?.push(currentPlayer?.let { PlayerPoint(it,point) })
        playersPointsLivedata.postValue(playersPoints)
    }

    fun removeLastPlayerPoint() {
        playersPoints?.pop()
        playersPointsLivedata.postValue(playersPoints)
    }

    fun getCurrentPlayer(): Player? {
        return currentPlayer
    }

    fun selectNextPlayer(): Player {
        currentPlayer = if (currentPlayer == null) {
            players.first()
        } else {
            players[(players.indexOf(currentPlayer)+1) % players.size]
        }
        return currentPlayer!!
    }

    fun getPlayersPoints() {
        playersPointsLivedata.postValue(playersPoints)
    }

}
