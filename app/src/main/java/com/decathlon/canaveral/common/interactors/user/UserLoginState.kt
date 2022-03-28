package com.decathlon.canaveral.common.interactors.user

import com.decathlon.core.user.data.UserRepository

class UserLoginState(private val userRepository: UserRepository) {

    fun isLogIn(): Boolean = userRepository.isLogIn()
    fun needRefreshToken(): Boolean = userRepository.needsTokenRefresh()
    suspend fun refreshToken() = userRepository.refreshToken()
    fun getAccessToken() = userRepository.getAccessToken()
}