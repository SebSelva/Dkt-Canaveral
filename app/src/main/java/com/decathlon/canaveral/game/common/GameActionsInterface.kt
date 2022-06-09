package com.decathlon.canaveral.game.common

import com.decathlon.canaveral.common.model.PlayerStats

interface GameActionsInterface {

    fun onGameEnd(duration:Long, winPlayers: MutableList<PlayerStats>)
}