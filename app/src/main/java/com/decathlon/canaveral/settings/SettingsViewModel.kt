package com.decathlon.canaveral.settings

import androidx.lifecycle.viewModelScope
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.interactors.Interactors
import kotlinx.coroutines.launch

class SettingsViewModel(private val interactors: Interactors): BaseViewModel<SettingsViewModel.SettingsUiState>() {

    fun getUserStats(accessToken: String) {
        viewModelScope.launch {
            //interactors.stdActions.getUserProfile(accessToken)
        }
    }

    sealed class SettingsUiState
}