package com.decathlon.core.gamestats.data.source.room

import android.content.Context
import com.decathlon.core.gamestats.data.source.room.entity.UserMeasureEntity

class LocalUserMeasureDataSource(val context: Context) {
    private val userMeasureDao = StatsDatabase.getDatabase(context).userMeasureDao()

    fun insert(measure: UserMeasureEntity) {
        userMeasureDao.insert(measure)
    }

    fun removeAll() {
        userMeasureDao.removeUserMeasures()
    }
}