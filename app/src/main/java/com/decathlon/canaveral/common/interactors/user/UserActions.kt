package com.decathlon.canaveral.common.interactors.user

import com.decathlon.core.user.data.UserRepository
import com.decathlon.core.user.model.User

class UserActions(private val userRepository: UserRepository) {

    suspend fun addUser(user: User) = userRepository.addUser(user)
    suspend fun getUsers() = userRepository.getUsers()
    suspend fun update(user: User) = userRepository.updateUser(user)
    suspend fun getMainUser(): User? = userRepository.getMainUser()
    suspend fun removeUser(user: User) = userRepository.removeUser(user)
    suspend fun removeMainUser() = userRepository.removeMainUser()
}