package com.decathlon.core.player.data.source

import com.decathlon.core.player.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayerDataSource {

    fun getPlayers(): Flow<List<Player>>

    suspend fun insertPlayer(player: Player)

    suspend fun removePlayer(player: Player)

    suspend fun getUserByNickname(nickname: String): List<Player>?

    suspend fun updatePlayer(player: Player)
}