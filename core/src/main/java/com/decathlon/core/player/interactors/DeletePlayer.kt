package com.decathlon.core.player.interactors

import com.decathlon.core.player.data.PlayerRepository
import com.decathlon.core.player.model.Player

class DeletePlayer (private val playerRepository: PlayerRepository) {

    suspend operator fun invoke(player: Player) =
        playerRepository.removePlayer(player)
}