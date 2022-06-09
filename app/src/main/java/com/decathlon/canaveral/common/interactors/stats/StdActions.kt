package com.decathlon.canaveral.common.interactors.stats

import com.decathlon.core.gamestats.data.STDRepository
import com.decathlon.core.gamestats.data.source.network.model.StdActivity

class StdActions(private val stdRepository: STDRepository) {

    fun getUserStats() =
        stdRepository.getAllStats()

    suspend fun updateStats(accessToken: String) =
        stdRepository.updateAllStats(accessToken)

    suspend fun updateActivity(stdActivity: StdActivity) =
        stdRepository.sendActivity(stdActivity)

    suspend fun deleteAllUserData() {
        stdRepository.removeAllStats()
    }
}