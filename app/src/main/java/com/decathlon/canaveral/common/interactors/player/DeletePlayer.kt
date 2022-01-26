package com.decathlon.canaveral.common.interactors.player

import com.decathlon.canaveral.common.model.Player
import com.decathlon.core.player.data.PlayerRepository

class DeletePlayer (private val playerRepository: PlayerRepository) {

    suspend operator fun invoke(player: Player) =
        playerRepository.removePlayer(player.toCore())
}