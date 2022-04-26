package com.decathlon.canaveral.common.interactors.player

import com.decathlon.canaveral.common.model.Player
import com.decathlon.core.player.data.PlayerRepository

class PlayerActions (private val playerRepository: PlayerRepository) {

    fun getPlayers() = playerRepository.getPlayers()

    suspend fun addPlayer(player: Player) =
        playerRepository.addPlayer(player.toCore())

    suspend fun deletePlayer(player: Player) =
        playerRepository.removePlayer(player.toCore())

    suspend fun updatePlayer(player: Player) =
        playerRepository.updatePlayer(player.toCore())
}