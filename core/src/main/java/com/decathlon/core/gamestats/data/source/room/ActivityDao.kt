package com.decathlon.core.gamestats.data.source.room

import androidx.room.Dao
import androidx.room.Query
import com.decathlon.core.common.data.source.room.BaseDao
import com.decathlon.core.gamestats.data.source.room.entity.GameActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao : BaseDao<GameActivityEntity> {

    @Query("SELECT * FROM activities")
    fun getActivities(): Flow<GameActivityEntity>

    @Query("DELETE FROM activities")
    fun removeActivities()
}