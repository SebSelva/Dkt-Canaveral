package com.decathlon.canaveral

import com.decathlon.canaveral.common.interactors.AddPlayer
import com.decathlon.canaveral.common.interactors.DeletePlayer
import com.decathlon.canaveral.common.interactors.GetPlayers

data class Interactors(
    val getPlayers: GetPlayers,
    val addPlayer: AddPlayer,
    val deletePlayer: DeletePlayer
)
