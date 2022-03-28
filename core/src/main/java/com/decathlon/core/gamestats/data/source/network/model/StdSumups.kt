package com.decathlon.core.gamestats.data.source.network.model

import com.google.gson.annotations.SerializedName

data class StdSumups (
    @SerializedName("hydra:member")
    val stdStat: List<StdStat>
)