package com.decathlon.canaveral.common.interactors.user

import com.decathlon.core.user.data.UserRepository
import com.decathlon.core.user.model.User

class AddUser(private val userRepository: UserRepository) {

    suspend operator fun invoke(user: User) = userRepository.addUser(user)
}