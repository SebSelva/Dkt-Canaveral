package com.decathlon.canaveral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.decathlon.core.gamestats.data.GeneralRepository
import com.decathlon.core.gamestats.data.IInternetAvailability
import com.decathlon.core.gamestats.data.STDRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class GameActivityViewModel(
    private val generalRepository: GeneralRepository,
    private val stdRepository: STDRepository,
) : ViewModel(), IInternetAvailability {

    private var hasInternet = true

    override fun onInternetAvailabilityChange(isAvailable: Boolean) {
        if (hasInternet != isAvailable && isAvailable) {
            Timber.d("onInternetAvailabilityChange $isAvailable")
            viewModelScope.launch {
               stdRepository.sendDataOff()
            }
        }
        hasInternet = isAvailable
    }

    fun registerConnection(){
        generalRepository.listener = this
        generalRepository.registerConnectivityListener()
        hasInternet = generalRepository.isConnected()
    }

    fun unRegisterConnection(){
        generalRepository.unRegisterConnectivityListener()
    }

}