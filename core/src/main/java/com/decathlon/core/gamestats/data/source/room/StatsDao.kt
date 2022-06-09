package com.decathlon.core.gamestats.data.source.room

import androidx.room.Dao
import androidx.room.Query
import com.decathlon.core.common.data.source.room.BaseDao
import com.decathlon.core.gamestats.data.source.room.entity.DartsStatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao: BaseDao<DartsStatEntity> {

    @Query("SELECT * FROM dartStats")
    fun getStats(): Flow<DartsStatEntity>

    @Query("DELETE FROM dartStats")
    fun removeAllStats()
}