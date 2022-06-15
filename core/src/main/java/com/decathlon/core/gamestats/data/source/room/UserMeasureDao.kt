package com.decathlon.core.gamestats.data.source.room

import androidx.room.Dao
import androidx.room.Query
import com.decathlon.core.common.data.source.room.BaseDao
import com.decathlon.core.gamestats.data.source.room.entity.UserMeasureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserMeasureDao : BaseDao<UserMeasureEntity> {

    @Query("SELECT * FROM user_measures")
    suspend fun getAllUserMeasures(): List<UserMeasureEntity>

    @Query("SELECT * FROM user_measures")
    fun getUserMeasures(): Flow<UserMeasureEntity>

    @Query("DELETE FROM user_measures")
    fun removeUserMeasures()
}