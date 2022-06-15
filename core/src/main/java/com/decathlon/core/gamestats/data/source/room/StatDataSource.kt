package com.decathlon.core.gamestats.data.source.room

import com.decathlon.core.gamestats.data.source.room.entity.DartsStatEntity
import kotlinx.coroutines.flow.Flow

interface StatDataSource {

    fun get(): Flow<DartsStatEntity>

    suspend fun getDartsStatEntity(): DartsStatEntity

    suspend fun insert(item: DartsStatEntity)

    suspend fun removeAll()
}