package com.decathlon.core.user.data.source.network

import com.decathlon.core.user.data.source.network.model.AccountInfo
import retrofit2.http.GET
import retrofit2.http.Header

interface STDServices {

    companion object {
        const val API_KEY_PREFIX = "x-api-key"
        const val ACCESS_TOKEN_PREFIX = "Authorization"
    }

    @GET("v2/me")
    suspend fun getAccountInfo(
        @Header(API_KEY_PREFIX) apiKey: String,
        @Header(ACCESS_TOKEN_PREFIX) bearer: String
    ): AccountInfo
}