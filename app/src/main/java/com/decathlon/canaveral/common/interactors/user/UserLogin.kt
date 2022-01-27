package com.decathlon.canaveral.common.interactors.user

import com.decathlon.core.user.common.AuthResource
import com.decathlon.core.user.common.Status
import com.decathlon.core.user.data.UserRepository
import com.decathlon.decathlonlogin.DktLoginState

class UserLogin(
    @PublishedApi
    internal val userRepository: UserRepository
    ) {

    suspend inline operator fun invoke(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result: AuthResource<DktLoginState> = userRepository.logInUser()
        when (result.status) {
            Status.SUCCESS -> onSuccess()
            Status.ERROR -> {
                if (UserRepository.isLoginError(result.dktLoginException)) {
                    onError(result.dktLoginException?.message ?: "")
                }
            }
        }
    }
}