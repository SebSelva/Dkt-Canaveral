package com.decathlon.canaveral.common.interactors.user

import com.decathlon.core.user.data.UserRepository
import com.decathlon.core.user.model.User

class UserLoginState(
    private val userRepository: UserRepository
) {
    suspend fun isLogIn(): Boolean = userRepository.isLogIn() && userRepository.isUserRegistered()
    fun needRefreshToken(): Boolean = userRepository.needsTokenRefresh()
    suspend fun getUser(): User? = userRepository.getMainUser()
    suspend fun getSTDAccountId(accessToken :String) = userRepository.getSTDId(accessToken)
    fun getAccessToken() = userRepository.getAccessToken()

}