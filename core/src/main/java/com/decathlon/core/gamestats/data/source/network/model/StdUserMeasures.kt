package com.decathlon.core.gamestats.data.source.network.model

import com.google.gson.annotations.SerializedName

data class StdUserMeasures (
    @SerializedName("hydra:member")
    val stdStat: List<StdFloatStat>
)