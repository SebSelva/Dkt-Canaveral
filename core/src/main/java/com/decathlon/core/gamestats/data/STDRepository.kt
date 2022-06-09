package com.decathlon.core.gamestats.data

import com.decathlon.core.Constants
import com.decathlon.core.gamestats.data.source.network.STDServices
import com.decathlon.core.gamestats.data.source.network.model.StdActivity
import com.decathlon.core.gamestats.data.source.room.LocalActivitiesDataSource
import com.decathlon.core.gamestats.data.source.room.StatDataSource
import com.decathlon.core.gamestats.data.source.room.entity.DartsStatEntity
import com.decathlon.core.gamestats.data.source.room.entity.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class STDRepository(
    private val stdApiKey: String,
    private val statsDataSource: StatDataSource,
    private val activitiesDataSource: LocalActivitiesDataSource
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

    fun getAllStats(): Flow<DartsStatEntity> = statsDataSource.get()

    private suspend fun removeAllStats() = statsDataSource.removeAll()

    private suspend fun getAccountInfo(accessToken: String) =
        stdServices.getAccountInfo(bearerHeader(accessToken))

    suspend fun updateAllStats(accessToken: String) = flow<Result<DartsStatEntity>> {
        val userRecJob = flowOf(refreshUserRecords(accessToken))
        val userLifeStats = flowOf(refreshUserLifeStats(accessToken))
        flowOf(userRecJob, userLifeStats).flattenMerge().collect { result ->
            if (result.isSuccessful && result.body() != null) {
                dartsStatEntity.setFromStatList(result.body()!!.stdStat)
            } else {
                emit(Result.failure(Throwable(result.code().toString())))
            }
        }
        refreshUserMeasures(accessToken).collect { result ->
            result.fold({
                saveStatsInDatabase()
                emit(Result.success(dartsStatEntity))
            }, {
                emit(Result.failure(it))
            })
        }
    }

    private suspend fun saveStatsInDatabase() {
        withContext(Dispatchers.IO) {
            dartsStatEntity.setDateTime()
            statsDataSource.insert(dartsStatEntity)
        }
    }

    private suspend fun refreshUserLifeStats(accessToken: String) =
        stdServices.getUserLifeStats(bearerHeader(accessToken))

    /*private suspend fun refreshUserLifeStats(accessToken: String) {
        val response = stdServices.getUserLifeStats(bearerHeader(accessToken))
        if (response.isSuccessful && response.body() != null) {
            dartsStatEntity.setFromStatList(response.body()!!.stdStat)
        }

    }*/

    private suspend fun refreshUserRecords(accessToken: String) =
        stdServices.getUserRecords(bearerHeader(accessToken))

    /*private suspend fun refreshUserRecords(accessToken: String) =
        flow {
            val response = stdServices.getUserRecords(bearerHeader(accessToken))
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Throwable(response.code().toString())))
            }
            //refreshUserMeasures(accessToken, userMeasuresIndexes.iterator())
        }*/

    private suspend fun refreshUserMeasures(accessToken: String) = flow {
        for (userMeasureDatatype in userMeasuresIndexes) {
            val response = stdServices.getUserMeasure(bearerHeader(accessToken), userMeasureDatatype)
            if (response.isSuccessful) {
                if (response.body() != null && response.body()!!.stdStat.isNotEmpty()) {
                    dartsStatEntity.setValueFromDatatype(response.body()!!.stdStat.first().datatype, response.body()!!.stdStat.first().value)
                }
            } else {
                emit(Result.failure(Exception(response.code().toString())))
            }
        }
        emit(Result.success(dartsStatEntity))
    }

    suspend fun postUserActivity(accessToken: String, stdActivity: StdActivity) =
        getAccountInfo(accessToken).let {
            stdActivity.user = it.id
            stdServices.postUserActivity(bearerHeader(accessToken), stdActivity)
        }

    fun saveActivity(stdActivity: StdActivity) {
        activitiesDataSource.insert(stdActivity.toEntity())
    }
}