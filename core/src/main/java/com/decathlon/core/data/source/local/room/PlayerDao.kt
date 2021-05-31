package com.decathlon.core.data.source.local.room

import androidx.room.*
import com.decathlon.core.data.source.local.entity.PlayerEntity


@Dao
interface PlayerDao : BaseDao<PlayerEntity> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayers(player: List<PlayerEntity>)

    @Query("SELECT * FROM player WHERE nickname LIKE :nickname")
    fun findUsersByNickname(nickname: String?): List<PlayerEntity?>?

    @Update
    fun updatePlayer(pl: PlayerEntity)

    @Delete
    fun deletePlayer(pl: PlayerEntity)



}