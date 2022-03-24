package com.decathlon.core.gamestats.data

import com.decathlon.core.Constants
import com.decathlon.core.gamestats.data.source.network.STDServices
import com.decathlon.core.gamestats.data.source.network.model.StdActivity
import com.decathlon.core.gamestats.data.source.room.StatDataSource
import com.decathlon.core.gamestats.data.source.room.entity.DartsStatEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class STDRepository(
    private val stdApiKey: String,
    private val statsDataSource: StatDataSource
) {
    private val stdServices: STDServices
    private fun bearerHeader(accessToken: String) = "Bearer $accessToken"
    private val dartsStatEntity = DartsStatEntity()

    init {
        val client = OkHttpClient.Builder().addInterceptor {
            val request = it.request().newBuilder()
                .addHeader(STDServices.API_KEY_PREFIX, stdApiKey)
                .build()
            return@addInterceptor it.proceed(request)
        }.build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(Constants.STD_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        stdServices = retrofit.create(STDServices::class.java)
    }

    private suspend fun getAccountInfo(accessToken: String) =
        stdServices.getAccountInfo(bearerHeader(accessToken))

    suspend fun getAllStats(accessToken: String): Flow<DartsStatEntity> {
        //statsDataSource.removeAll()
        val allStats = statsDataSource.get()
        //dartsStatEntity = DartsStatEntity(allStats.first())
        refreshUserRecords(accessToken)
        return allStats
    }

    private suspend fun refreshUserLifeStats(accessToken: String) {
        val response = stdServices.getUserLifeStats(bearerHeader(accessToken))
        if (response.isSuccessful && response.body() != null) {
            dartsStatEntity.setFromStdStatList(response.body()!!.stdStat)
        }
        withContext(Dispatchers.IO) {
            statsDataSource.insert(dartsStatEntity)
        }
        refreshUserMeasures(accessToken)
    }

    private suspend fun refreshUserRecords(accessToken: String) {
        val response = stdServices.getUserRecords(bearerHeader(accessToken))
        if (response.isSuccessful && response.body() != null) {
            dartsStatEntity.setFromStdStatList(response.body()!!.stdStat)
        }
        refreshUserLifeStats(accessToken)
    }

    private suspend fun refreshUserMeasures(accessToken: String) {
        val response = stdServices.getUserMeasures(bearerHeader(accessToken))
        if (response.isSuccessful && response.body() != null) {
            dartsStatEntity.setFromStdStatList(response.body()!!.stdStat)
        }
    }

    suspend fun postUserActivity(accessToken: String, stdActivity: StdActivity) =
        getAccountInfo(accessToken).let {
            stdServices.postUserActivity(bearerHeader(accessToken), stdActivity)
        }
}