package com.decathlon.core.player.data

import com.decathlon.core.player.data.source.PlayerDataSource
import com.decathlon.core.player.model.Player

class PlayerRepository(private val playerDataSource: PlayerDataSource) {

    suspend fun addPlayer(player: Player) =
        playerDataSource.insertPlayer(player)

    suspend fun removePlayer(player: Player) =
        playerDataSource.removePlayer(player)

    fun getPlayers() =
        playerDataSource.getPlayers()
}