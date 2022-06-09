package com.decathlon.core.gamestats.data

import com.decathlon.core.Constants
import com.decathlon.core.gamestats.data.source.network.STDServices
import com.decathlon.core.gamestats.data.source.network.model.StdActivity
import com.decathlon.core.gamestats.data.source.network.model.StdUserMeasure
import com.decathlon.core.gamestats.data.source.network.model.getDartCount
import com.decathlon.core.gamestats.data.source.room.LocalActivitiesDataSource
import com.decathlon.core.gamestats.data.source.room.LocalUserMeasureDataSource
import com.decathlon.core.gamestats.data.source.room.StatDataSource
import com.decathlon.core.gamestats.data.source.room.entity.*
import com.decathlon.core.user.data.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import kotlin.math.roundToInt

class STDRepository(
    private val stdApiKey: String,
    private val statsDataSource: StatDataSource,
    private val activitiesDataSource: LocalActivitiesDataSource,
    private val generalRepository: GeneralRepository,
    private val measureDataSource: LocalUserMeasureDataSource,
    private val userRepository: UserRepository
) : IInternetAvailability {
    private val stdServices: STDServices
    private fun bearerHeader(accessToken: String) = "Bearer $accessToken"
    private val dartsStatEntity = DartsStatEntity()

    private var isConnected = true

    private var jobDataOffSent: Job? = null

    var scope = CoroutineScope(IO)

    private val userMeasuresIndexes = mutableListOf(
        219, 222, 284, 285, 286, 287, 288, 289,
        290, 291, 292, 293, 294, 295, 296, 297, 298, 299,
        300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310
    )

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


        generalRepository.listener = this
        generalRepository.registerConnectivityListener()
        isConnected = generalRepository.isConnected
        Timber.i("init CONNECTION change $isConnected")
        if (isConnected) sendDataOff()
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
            val response =
                stdServices.getUserMeasure(bearerHeader(accessToken), userMeasureDatatype)
            if (response.isSuccessful) {
                if (response.body() != null && response.body()!!.stdStat.isNotEmpty()) {
                    dartsStatEntity.setValueFromDatatype(
                        response.body()!!.stdStat.first().datatype,
                        response.body()!!.stdStat.first().value
                    )
                }
            } else {
                emit(Result.failure(Exception(response.code().toString())))
            }
        }
        emit(Result.success(dartsStatEntity))
    }

    suspend fun sendActivity(accessToken: String, stdActivity: StdActivity) {
        if (isConnected) {
            getAccountInfo(accessToken).let {
                stdActivity.user = it.id
                Timber.d("POST ACTIVITY $stdActivity")
                //stdServices.postUserActivity(bearerHeader(accessToken), stdActivity)
                getStdUserMeasures(it.id,stdActivity).forEach { userMeasure->
                    Timber.d("POST USER MEASURE $userMeasure")
                    //stdServices.postUserMeasure(bearerHeader(accessToken), userMeasure)
                }
            }
        } else {
            activitiesDataSource.insert(stdActivity.toEntity())
            getUserMeasureEntities().forEach {
                measureDataSource.insert(it)
            }
        }
    }

    private suspend fun getStdUserMeasures(
        userId: String,
        stdActivity: StdActivity
    ): List<StdUserMeasure> {
        val list = mutableListOf<StdUserMeasure>()
        val dartsStatEntity = statsDataSource.getDartsStatEntity()
        val newDartStats = stdActivity.dataSummaries
        userMeasuresIndexes.forEach { measureIndex ->
            when (measureIndex) {
                GAMES_WON_PERCENT -> {
                    val t = dartsStatEntity.gamesPlayed + 1
                    val newPercent = if (t == 0L) 0f else
                        (dartsStatEntity.gamesWon + newDartStats.gamesWon) * 100f / t
                    val userMeasure = StdUserMeasure(
                        userId,
                        measureIndex.toString(),
                        newPercent.roundToInt().toFloat(),
                        stdActivity.startDate
                    )
                    list.add(userMeasure)
                }
                LEGS_WON_PERCENT -> {
                    val t = dartsStatEntity.legsPlayed + newDartStats.legsPlayed
                    val newPercent = if (t == 0L) 0f else
                        (dartsStatEntity.legsWon + newDartStats.legsWon) * 100f / t
                    val userMeasure = StdUserMeasure(
                        userId,
                        measureIndex.toString(),
                        newPercent.roundToInt().toFloat(),
                        stdActivity.startDate
                    )
                    list.add(userMeasure)
                }
                else -> {

                    val indexCount = StdUtils.getCountIndexFromPercentIndex(measureIndex)
                    if (indexCount != -1) {
                        val newPercent =
                            (dartsStatEntity.getDartCount(indexCount) + newDartStats.getDartCount(
                                indexCount
                            )) * 100f /
                                    (dartsStatEntity.dartsThrown + newDartStats.dartsThrown)
                        val userMeasure = StdUserMeasure(
                            userId, measureIndex.toString(), newPercent.roundToInt().toFloat(),
                            stdActivity.startDate
                        )
                        list.add(userMeasure)
                    }
                }
            }
        }
        return list
    }

    private suspend fun getUserMeasureEntities(): List<UserMeasureEntity> {
        val list = mutableListOf<UserMeasureEntity>()
        val dartsStatEntity = statsDataSource.getDartsStatEntity()
        val activities = activitiesDataSource.getActivities()
        userMeasuresIndexes.forEach { measureIndex ->
            when (measureIndex) {
                GAMES_WON_PERCENT -> {
                    val totalWon = activities.size
                    val total = activities.sumOf { it.dataSummaries.gamesWon }
                    val t = dartsStatEntity.gamesPlayed + total
                    val newPercent = if (t == 0L) 0f else
                        (dartsStatEntity.gamesWon + totalWon) * 100f / t
                    val userMeasure = UserMeasureEntity(
                        datatype = measureIndex.toString(),
                        value = newPercent.roundToInt().toFloat(),
                        date = activities.last().startDate
                    )
                    list.add(userMeasure)
                }
                LEGS_WON_PERCENT -> {
                    val totalWon = activities.sumOf { it.dataSummaries.legsPlayed }
                    val total = activities.sumOf { it.dataSummaries.legsWon }
                    val t = dartsStatEntity.legsPlayed + total
                    val newPercent = if (t == 0L) 0f else
                        (dartsStatEntity.legsWon + totalWon) * 100f / t
                    val userMeasure = UserMeasureEntity(
                        datatype = measureIndex.toString(),
                        value = newPercent.roundToInt().toFloat(),
                        date = activities.last().startDate
                    )
                    list.add(userMeasure)
                }
                else -> {
                    val indexCount = StdUtils.getCountIndexFromPercentIndex(measureIndex)
                    if (indexCount != -1) {
                        val totalCountActivities =
                            activities.sumOf { it.dataSummaries.getDartCount(indexCount) }
                        val totalThrownActivities =
                            activities.sumOf { it.dataSummaries.dartsThrown }
                        val newPercent =
                            (dartsStatEntity.getDartCount(indexCount) + totalCountActivities) * 100f /
                                    (dartsStatEntity.dartsThrown + totalThrownActivities)
                        val userMeasure = UserMeasureEntity(
                            datatype = measureIndex.toString(),
                            value = newPercent.roundToInt().toFloat(),
                            date = activities.last().startDate
                        )
                        list.add(userMeasure)
                    }
                }
            }
        }
        return list
    }

    override fun onInternetAvailabilityChange(isAvailable: Boolean) {
        if (isConnected != isAvailable && isAvailable) {
            Timber.d("connection internet change $isAvailable")
            sendDataOff()
        }
        isConnected = isAvailable
    }

    private fun sendDataOff() {
        scope.launch {
            val bearer = userRepository.getAccessToken()
            bearer?.let { token ->
                getAccountInfo(token).let { info ->
                    activitiesDataSource.getActivities().forEach {
                        stdServices.postUserActivity(token, it.toWs(info.id))
                    }
                    measureDataSource.getUserMeasures().forEach {
                        stdServices.postUserMeasure(token, it.toWs(info.id))
                    }
                    activitiesDataSource.removeAll()
                    measureDataSource.removeAll()
                    jobDataOffSent = null
                    updateAllStats(token)
                }
            }
        }
    }
}