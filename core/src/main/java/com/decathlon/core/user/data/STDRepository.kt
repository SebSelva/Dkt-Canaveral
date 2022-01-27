package com.decathlon.core.user.data

import com.decathlon.core.user.data.source.network.STDServices
import com.decathlon.core.user.data.source.network.model.AccountInfo

class STDRepository(
    private val stdServices: STDServices
) {
    suspend fun getAccountInfo(
        accessToken: String,
        apiKey: String
    ): AccountInfo = stdServices.getAccountInfo(apiKey,"Bearer $accessToken")
}