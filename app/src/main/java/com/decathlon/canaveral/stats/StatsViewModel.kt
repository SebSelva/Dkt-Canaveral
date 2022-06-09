package com.decathlon.canaveral.stats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.interactors.Interactors
import com.decathlon.core.gamestats.data.source.room.entity.DartsStatEntity
import kotlinx.coroutines.launch

class StatsViewModel(private val interactors: Interactors): BaseViewModel<StatsViewModel.StatsViewState>() {

    val statsLiveData : MutableLiveData<DartsStatEntity> = MutableLiveData()

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

    fun getStats() {
        viewModelScope.launch {
            setUiState(StatsViewState.StatsUpdate)
            interactors.stdActions.getUserStats().collect {
                statsLiveData.postValue(it)
                setUiState(StatsViewState.StatsComplete)
            }
        }
    }

    suspend fun updateStats() {
        setUiState(StatsViewState.StatsUpdate)
        viewModelScope.launch {
            getAccessToken()?.let { token ->
                interactors.stdActions.updateStats(token).collect {
                    setUiState(if (it.isSuccess)
                        StatsViewState.StatsComplete
                    else
                        StatsViewState.StatsNetworkError(it.exceptionOrNull()!!.message!!))
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
        data class StatsNetworkError(val errorCode: String): StatsViewState()
    }
}