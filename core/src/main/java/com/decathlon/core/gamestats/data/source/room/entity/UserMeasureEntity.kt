package com.decathlon.core.gamestats.data.source.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_measures", indices = [Index(value = ["datatype", "date"], unique = true)])
data class UserMeasureEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    val datatype: String,
    val value: Float,
    val date: String
)

