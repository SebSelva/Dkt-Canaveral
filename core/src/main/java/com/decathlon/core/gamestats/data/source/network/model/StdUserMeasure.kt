package com.decathlon.core.gamestats.data.source.network.model

data class StdUserMeasure(
    var user: String,
    val datatype: String,
    val value: Float,
    val date: String
)
