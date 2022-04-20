package com.decathlon.core.gamestats.data.source.network.model

data class StdUserMeasure(
    val user: String,
    val datatype: String,
    val value: Float,
    val date: String
)
