package com.decathlon.canaveral.settings

import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.interactors.Interactors

class SettingsViewModel(private val interactors: Interactors): BaseViewModel<SettingsViewModel.SettingsUiState>() {

    private fun settingsState(state: SettingsUiState) {
        uiState.value = state
    }

    sealed class SettingsUiState {

    }
}