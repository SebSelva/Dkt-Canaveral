package com.decathlon.canaveral.game.x01

import androidx.lifecycle.MutableLiveData
import com.decathlon.canaveral.Interactors
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.model.NullPoint
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.model.PlayerPoint
import com.decathlon.canaveral.common.model.Point
import com.decathlon.canaveral.common.utils.DartsUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*

class Game01ViewModel(private val interactors: Interactors) : BaseViewModel() {

    var players: List<Player> = emptyList()
    var startingPoints = 0
    var nbRounds: Int? = 0
    var isBull25 = true
    var inValue = 0
    var outValue = 0

    var playersPoints: Stack<PlayerPoint> = Stack()
    var currentPlayer: Player? = null
    var currentRound: Int = 1
    var isStackIncreasing: Boolean = true
    var isRoundDecreasing: Boolean = false
    var isRoundBusted = false

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
            if (DartsUtils.getPlayerRoundDarts(it, currentRound, playersPoints).size < DartsUtils.DARTS_SHOTS_NUMBER) {
                currentRound = DartsUtils.getRoundNumber(players, playersPoints)
                isRoundBusted = DartsUtils.isBusted(it, playersPoints, point, startingPoints, isBull25, inValue, outValue)
                playersPoints.push(PlayerPoint(it, point, currentRound, isRoundBusted))
                if (isRoundBusted) addPlayerRoundBusted(currentRound, it)
                playersPointsLivedata.postValue(playersPoints)
            }
        }
    }

    private fun addPlayerRoundBusted(currentRound: Int, currentPlayer: Player) {
        playersPoints.filter { it.round == currentRound && it.player == currentPlayer }
            .forEach {
                it.isBusted = true
            }
        val nullDartsNumber = DartsUtils.DARTS_SHOTS_NUMBER - playersPoints.filter { it.round == currentRound && it.player == currentPlayer }.size
        repeat(nullDartsNumber) {
            playersPoints.push(PlayerPoint(currentPlayer, NullPoint(), currentRound, true))
        }
    }

    private fun removePlayerRoundBusted(currentRound: Int, currentPlayer: Player?) {
        playersPoints.filter { it.round == currentRound && it.player == currentPlayer }
            .forEach {
                it.isBusted = false
            }
    }

    fun removeLastPlayerPoint() {
        isStackIncreasing = false
        isRoundDecreasing = false
        if (playersPoints.isNotEmpty()) {
            if (currentPlayer != playersPoints.peek().player || currentRound != playersPoints.peek().round) {
                currentPlayer = playersPoints.peek().player
                currentRound = playersPoints.peek().round
                isRoundBusted = playersPoints.peek().isBusted
                isRoundDecreasing = true
                currentPlayerLiveData.postValue(currentPlayer)
            } else {
                var isNullDart = true
                while (isNullDart) {
                    isNullDart = playersPoints.pop().point is NullPoint
                }
                removePlayerRoundBusted(currentRound, currentPlayer)
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
