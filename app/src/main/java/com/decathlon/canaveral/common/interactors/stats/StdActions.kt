package com.decathlon.canaveral.common.interactors.stats

import com.decathlon.core.gamestats.data.STDRepository
import com.decathlon.core.gamestats.data.source.network.model.StdActivity

class StdActions(private val stdRepository: STDRepository) {

    suspend fun getUserStats(accessToken: String) =
        stdRepository.getAllStats(accessToken)

    suspend fun postUserActivity(accessToken: String, stdActivity: StdActivity) =
        stdRepository.postUserActivity(accessToken, stdActivity)
}