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

    private val userMeasuresIndexes = mutableListOf(
        219, 222, 284, 285, 286, 287, 288, 289,
        290, 291, 292, 293, 294, 295, 296, 297, 298, 299,
        300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310)

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
            dartsStatEntity.setFromStatList(response.body()!!.stdStat)
        }
        withContext(Dispatchers.IO) {
            dartsStatEntity.setDateTime()
            statsDataSource.insert(dartsStatEntity)
        }
    }

    private suspend fun refreshUserRecords(accessToken: String) {
        val response = stdServices.getUserRecords(bearerHeader(accessToken))
        if (response.isSuccessful && response.body() != null) {
            dartsStatEntity.setFromStatList(response.body()!!.stdStat)
        }
        refreshUserMeasures(accessToken, userMeasuresIndexes.iterator())
    }

    private suspend fun refreshUserMeasures(accessToken: String, iterator: Iterator<Int>) {
        if (iterator.hasNext()) {
            val userMeasureDatatype = iterator.next()
            val response = stdServices.getUserMeasure(bearerHeader(accessToken), userMeasureDatatype)
            if (response.isSuccessful) {
                if (response.body() != null && response.body()!!.stdStat.isNotEmpty()) {
                    dartsStatEntity.setValueFromDatatype(response.body()!!.stdStat.first().datatype, response.body()!!.stdStat.first().value)
                }
                refreshUserMeasures(accessToken, iterator)
            }
        } else {
            refreshUserLifeStats(accessToken)
        }

    }

    suspend fun postUserActivity(accessToken: String, stdActivity: StdActivity) =
        getAccountInfo(accessToken).let {
            stdServices.postUserActivity(bearerHeader(accessToken), stdActivity)
        }
}