package com.decathlon.core.gamestats.data.source.network

import com.decathlon.core.gamestats.data.source.network.model.AccountInfo
import com.decathlon.core.gamestats.data.source.network.model.StdActivity
import com.decathlon.core.gamestats.data.source.network.model.StdSumups
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface STDServices {

    companion object {
        const val API_KEY_PREFIX = "x-api-key"
        const val ACCESS_TOKEN_PREFIX = "Authorization"
        const val DARTS_SPORT_ID = 316
    }

    @GET("v2/me")
    suspend fun getAccountInfo(
        @Header(ACCESS_TOKEN_PREFIX) bearer: String
    ): AccountInfo

    @GET("current_user_measures")
    suspend fun getUserMeasures(
        @Header(ACCESS_TOKEN_PREFIX) bearer: String
    ): Response<StdSumups>

    @GET("v2/user_records?sport=$DARTS_SPORT_ID")
    suspend fun getUserRecords(
        @Header(ACCESS_TOKEN_PREFIX) bearer: String
    ): Response<StdSumups>

    @GET("v2/user_sumups?sport=$DARTS_SPORT_ID&type=LifelySumup")
    suspend fun getUserLifeStats(
        @Header(ACCESS_TOKEN_PREFIX) bearer: String
    ): Response<StdSumups>

    @POST("v2/activities")
    suspend fun postUserActivity(
        @Header(ACCESS_TOKEN_PREFIX) bearer: String,
        @Body activity: StdActivity
    )
}