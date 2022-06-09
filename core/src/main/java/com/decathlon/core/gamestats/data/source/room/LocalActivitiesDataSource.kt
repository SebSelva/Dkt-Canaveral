package com.decathlon.core.gamestats.data.source.room

import android.content.Context
import com.decathlon.core.gamestats.data.source.room.entity.GameActivityEntity

class LocalActivitiesDataSource(val context: Context) {
    private val activityDao = StatsDatabase.getDatabase(context).activityDao()

    fun insert(activity: GameActivityEntity) {
        activityDao.insert(activity)
    }

    fun removeAll() {
        activityDao.removeActivities()
    }
}