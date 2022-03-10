package com.decathlon.core.gamestats.data

import com.decathlon.core.gamestats.data.source.network.STDServices
import com.decathlon.core.gamestats.data.source.network.model.StdSumups

class STDRepository(
    private val stdApiKey: String,
    private val stdServices: STDServices
) {
/*suspend fun getAccountInfo(accessToken: String): AccountInfo {
    suspendCoroutine<AccountInfo> { continuation ->
        CorouteScope.launch {
            stdServices.getAccountInfo(stdApiKey,"Bearer $accessToken")
        }
    }
}*/

    suspend fun getUserLifeStats(accessToken: String): StdSumups {
        return stdServices.getUserLifeStats(stdApiKey, "Bearer $accessToken")
    }
}