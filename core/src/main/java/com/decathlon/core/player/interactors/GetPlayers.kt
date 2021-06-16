package com.decathlon.core.player.interactors

import com.decathlon.core.player.data.PlayerRepository

class GetPlayers (private val playerRepository: PlayerRepository) {

    suspend operator fun invoke() =
        playerRepository.getPlayers()
}