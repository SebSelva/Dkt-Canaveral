package com.decathlon.canaveral

import com.decathlon.core.player.interactors.AddPlayer
import com.decathlon.core.player.interactors.DeletePlayer
import com.decathlon.core.player.interactors.GetPlayers

data class Interactors(
    val getPlayers: GetPlayers,
    val addPlayer: AddPlayer,
    val deletePlayer: DeletePlayer
)
