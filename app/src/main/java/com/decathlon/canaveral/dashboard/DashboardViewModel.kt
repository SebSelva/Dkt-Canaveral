package com.decathlon.canaveral.dashboard

import android.app.Application
import androidx.lifecycle.*
import com.decathlon.canaveral.CanaveralViewModel
import com.decathlon.canaveral.Interactors
import com.decathlon.core.player.data.PlayerRepository
import com.decathlon.core.player.model.Player
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardViewModel (application: Application, interactors: Interactors)
    : CanaveralViewModel(application, interactors) {

    val playerLiveData : MutableLiveData<List<Player>> = MutableLiveData()

    fun getPlayers() = GlobalScope.launch {
        interactors.getPlayers().collect {
            playerLiveData.postValue(it)
        }
    }

    fun addPlayer(player: Player) = GlobalScope.launch {
        interactors.addPlayer(player)
    }

    fun removePlayer(player: Player) = GlobalScope.launch {

    }
}