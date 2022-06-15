package com.decathlon.canaveral.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.decathlon.canaveral.common.interactors.Interactors
import com.decathlon.canaveral.common.model.PlayerStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

open class BaseViewModel<T> : ViewModel(), CoroutineScope {

    fun uiState(): LiveData<T> = uiState
    protected val uiState: MutableLiveData<T> = MutableLiveData()

    fun setUiState(uiModelState: T) { uiState.value = uiModelState!! }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob()

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }

    open fun onGameEnd(duration:Long, winPlayers: MutableList<PlayerStats>){
        // TODO Not implemented
    }

    open suspend fun getAccessToken(interactors:Interactors): String? {
        if (interactors.userLoginState.isLogIn()) {
            if (interactors.userLoginState.needRefreshToken()) {
                interactors.userLoginState.refreshToken()
            }
            return interactors.userLoginState.getAccessToken()
        }
        return null
    }
}
