package com.decathlon.core.player.data.source

import android.content.Context
import com.decathlon.core.player.data.entity.PlayerEntity
import com.decathlon.core.player.data.source.room.PlayerDatabase
import com.decathlon.core.player.model.Player
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RoomPlayerDataSource(context: Context) : PlayerDataSource {

    private val playerDao = PlayerDatabase.getDatabase(context).playerDao()

    override fun getPlayers() =
        playerDao.getPlayers().map { it.map { playerEntity -> Player(playerEntity) } }

    override suspend fun insertPlayer(player: Player) {
        if (player.nickname.isEmpty()) {
            val players = getPlayers().first()
            val nb = players.count()
            (1..nb+1).forEach {
                var found = false
                players.forEach { dbPlayer ->
                    found = found || dbPlayer.nickname.endsWith(it.toString(), false)
                }
                if (!found && player.nickname.isEmpty()) {
                    player.nickname = "Player $it"
                    return@forEach
                }
            }
        }
        playerDao.insert(PlayerEntity(player))
    }

    override suspend fun removePlayer(player: Player): Unit =
        playerDao.deletePlayer(PlayerEntity(player))

    override suspend fun getEntityById(id: Long): PlayerEntity? =
        playerDao.findUserById(id)

    override suspend fun getUserByNickname(nickname: String) =
        playerDao.findUsersByNickname(nickname)?.map {playerEntity -> Player(playerEntity) }

    override suspend fun updatePlayer(player: Player) =
        playerDao.updatePlayer(PlayerEntity(player))

}