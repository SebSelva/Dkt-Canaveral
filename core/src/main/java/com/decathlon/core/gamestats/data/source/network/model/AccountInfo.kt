package com.decathlon.core.gamestats.data.source.network.model

import com.google.gson.annotations.SerializedName

data class AccountInfo(
    @SerializedName("@id")
    val id: String
)
