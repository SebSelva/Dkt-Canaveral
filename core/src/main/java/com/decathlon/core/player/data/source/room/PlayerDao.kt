package com.decathlon.core.player.data.source.room

import androidx.room.*
import com.decathlon.core.common.data.source.room.BaseDao
import com.decathlon.core.player.data.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PlayerDao : BaseDao<PlayerEntity> {

    @Query("SELECT * FROM player")
    fun getPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM player WHERE uid LIKE :id")
    fun findUsersById(id: String?): List<PlayerEntity?>?

    @Query("SELECT * FROM player WHERE nickname LIKE :nickname")
    fun findUsersByNickname(nickname: String?): List<PlayerEntity>?

    @Update
    fun updatePlayer(pl: PlayerEntity)

    @Delete
    fun deletePlayer(pl: PlayerEntity)

}
