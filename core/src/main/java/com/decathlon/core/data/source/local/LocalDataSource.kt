package com.decathlon.core.data.source.local

import com.decathlon.core.data.source.local.entity.PlayerEntity
import com.decathlon.core.data.source.local.room.PlayerDao

class LocalDataSource(private val playerDao: PlayerDao) {

    fun insertPlayer(player: PlayerEntity) = playerDao.insert(player)

    suspend fun insertPlayers(playerList: List<PlayerEntity>) = playerDao.insertPlayers(playerList)

    fun getUserByNickname(nickname: String) {
        playerDao.findUsersByNickname(nickname)
    }

    fun updatePlayer(player: PlayerEntity, newNickname: String) {
        player.nickname = newNickname
        playerDao.updatePlayer(player)
    }

}