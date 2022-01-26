package com.decathlon.canaveral.common.interactors.player

import com.decathlon.core.player.data.PlayerRepository

class GetPlayers (private val playerRepository: PlayerRepository) {

    operator fun invoke() =
        playerRepository.getPlayers()
}