package com.decathlon.canaveral.dashboard

import androidx.lifecycle.*
import com.decathlon.canaveral.CanaveralViewModel
import com.decathlon.canaveral.Interactors
import com.decathlon.core.player.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardViewModel (private val interactors: Interactors) : CanaveralViewModel() {

    val playerLiveData : MutableLiveData<List<Player>> = MutableLiveData()

    fun getPlayers() = GlobalScope.launch(Dispatchers.IO) {
        interactors.getPlayers().collect {
            playerLiveData.postValue(it)
        }
    }

    fun addPlayer(player: Player) = viewModelScope.launch(Dispatchers.IO) {
        interactors.addPlayer(player)
    }

}