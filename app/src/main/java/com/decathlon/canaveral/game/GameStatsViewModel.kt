package com.decathlon.canaveral.game

import androidx.lifecycle.ViewModel
import com.decathlon.canaveral.common.model.PlayerStats

class GameStatsViewModel : ViewModel() {

    var winPlayers: MutableList<PlayerStats> = emptyList<PlayerStats>().toMutableList()

}