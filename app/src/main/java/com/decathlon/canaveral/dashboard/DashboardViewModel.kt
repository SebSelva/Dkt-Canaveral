package com.decathlon.canaveral.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.decathlon.canaveral.Interactors
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardViewModel (private val interactors: Interactors) : BaseViewModel() {

    var gameTypeIndex = 0
    val playerLiveData : MutableLiveData<List<Player>> = MutableLiveData()

    fun getPlayers() = viewModelScope.launch(Dispatchers.IO) {
        interactors.getPlayers().collect {
            playerLiveData.postValue(it.map { player -> Player(player) })
        }
    }

    fun addPlayer(player: Player) = viewModelScope.launch(Dispatchers.IO) {
        interactors.addPlayer(player)
    }

    fun removePlayer(player: Player) = viewModelScope.launch(Dispatchers.IO) {
        interactors.deletePlayer(player)
    }

    fun updatePlayer(player: Player) = viewModelScope.launch(Dispatchers.IO) {
        interactors.updatePlayer(player)
    }
}
