package com.decathlon.core.gamestats.data.source.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.decathlon.core.gamestats.data.source.network.model.StdUserMeasure

@Entity(tableName = "user_measures")
data class UserMeasureEntity(
    @PrimaryKey
    var id: Long = 0L,
    val datatype: String,
    val value: Float,
    val date: String
)

fun UserMeasureEntity.toWs(user: String): StdUserMeasure {
    return StdUserMeasure(user, datatype, value, date)
}

