package com.decathlon.core.gamestats.data.source.network.model

data class StdActivity(
    val name: String,
    var user: String,
    val sport: String,
    val startDate: String,
    val duration: Long,
    val connector: String,
    val dataSummaries: StdDartsData
)
