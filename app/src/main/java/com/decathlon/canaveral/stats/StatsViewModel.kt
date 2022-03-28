package com.decathlon.canaveral.stats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.interactors.Interactors
import com.decathlon.core.gamestats.data.source.room.entity.DartsStatEntity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class StatsViewModel(private val interactors: Interactors): BaseViewModel<StatsViewModel.StatsViewState>() {

    val statsLiveData : MutableLiveData<DartsStatEntity> = MutableLiveData()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.d("Network error: $throwable - ${throwable.stackTrace.contentToString()}")
        setUiState(StatsViewState.StatsNetworkError(Exception(throwable)))
    }

    private suspend fun getAccessToken(): String? {
        if (interactors.userLoginState.isLogIn()) {
            if (interactors.userLoginState.needRefreshToken()) {
                interactors.userLoginState.refreshToken()
            }
            return interactors.userLoginState.getAccessToken()
        }
        setUiState(StatsViewState.StatsUserLogout)
        return null
    }

    suspend fun getStats() {
        setUiState(StatsViewState.StatsUpdate)
        viewModelScope.launch(errorHandler) {
            getAccessToken()?.let { token ->
                interactors.stdActions.getUserStats(token).collect {
                    statsLiveData.postValue(it)
                    setUiState(StatsViewState.StatsComplete)
                }
            }
        }
    }

    /*fun postActivity(stdActivity: StdActivity) {

    }*/

    sealed class StatsViewState {
        object StatsUserLogout: StatsViewState()
        object StatsUpdate: StatsViewState()
        object StatsComplete: StatsViewState()
        data class StatsNetworkError(val e: Exception): StatsViewState()
    }
}