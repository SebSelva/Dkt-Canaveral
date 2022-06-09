package com.decathlon.core.gamestats.data.source.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_measures")
data class UserMeasureEntity(
    @PrimaryKey
    var id: Long = 0L,
    val datatype: String,
    val value: Float,
    val date: String
)

