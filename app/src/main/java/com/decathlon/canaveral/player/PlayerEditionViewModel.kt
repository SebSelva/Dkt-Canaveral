package com.decathlon.canaveral.player

import androidx.lifecycle.viewModelScope
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.interactors.Interactors
import com.decathlon.canaveral.common.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayerEditionViewModel(private val interactors: Interactors) : BaseViewModel<PlayerEditionViewModel.PlayerEditionState>() {

    lateinit var player: Player

    fun updatePlayer() = viewModelScope.launch(Dispatchers.IO) {
        interactors.playerActions.updatePlayer(player)
    }

    sealed class PlayerEditionState {
    }
}