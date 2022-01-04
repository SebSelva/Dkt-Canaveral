package com.decathlon.canaveral.common.interactors

import com.decathlon.canaveral.common.model.Player
import com.decathlon.core.player.data.PlayerRepository

class UpdatePlayer (private val playerRepository: PlayerRepository) {

    suspend operator fun invoke(player: Player) =
        playerRepository.updatePlayer(player.toCore())
}