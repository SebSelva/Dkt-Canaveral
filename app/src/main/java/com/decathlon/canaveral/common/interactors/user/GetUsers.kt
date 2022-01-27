package com.decathlon.canaveral.common.interactors.user

import com.decathlon.core.user.data.UserRepository

class GetUsers(private val userRepository: UserRepository) {

    suspend operator fun invoke() =
        userRepository.getUsers()
}