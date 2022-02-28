package com.decathlon.canaveral.settings

import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.interactors.Interactors

class SettingsViewModel(private val interactors: Interactors): BaseViewModel<SettingsViewModel.SettingsUiState>() {

    sealed class SettingsUiState
}