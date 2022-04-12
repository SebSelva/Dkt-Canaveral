package com.decathlon.core.gamestats.data.source.network

import com.decathlon.core.gamestats.data.source.network.model.*
import retrofit2.Response
import retrofit2.http.*

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

    @GET("v2/user_measures")
    suspend fun getUserMeasure(
        @Header(ACCESS_TOKEN_PREFIX) bearer: String,
        @Query("datatype") datatype: Int
    ): Response<StdUserMeasures>

    @POST("v2/user_measures")
    suspend fun postUserMeasure(
        @Header(ACCESS_TOKEN_PREFIX) bearer: String,
        @Body userMeasure: StdUserMeasure
    ): Response<StdUserMeasure>

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