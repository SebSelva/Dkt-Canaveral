package com.decathlon.core.gamestats.data.source.room

import android.content.Context
import com.decathlon.core.gamestats.data.source.room.entity.DartsStatEntity
import kotlinx.coroutines.flow.Flow

class RoomStatsDataSource(context: Context): StatDataSource {

    private val statsDao = StatsDatabase.getDatabase(context).statsDao()

    override fun get(): Flow<DartsStatEntity> = statsDao.getStats()

    override suspend fun getDartsStatEntity(): DartsStatEntity {
        return statsDao.getDartsStatEntity()
    }

    override suspend fun insert(item: DartsStatEntity) {
        statsDao.insert(item)
    }

    override suspend fun removeAll() {
        statsDao.removeAllStats()
    }
}