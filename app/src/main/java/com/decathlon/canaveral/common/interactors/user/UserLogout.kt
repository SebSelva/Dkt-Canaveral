package com.decathlon.canaveral.common.interactors.user

import com.decathlon.core.user.common.AuthResource
import com.decathlon.core.user.common.Status
import com.decathlon.core.user.data.UserRepository
import com.decathlon.decathlonlogin.DktLoginState
import timber.log.Timber

class UserLogout(
    @PublishedApi
    internal val userRepository: UserRepository
    ) {

    suspend inline operator fun invoke(
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val result: AuthResource<DktLoginState> = userRepository.logOutUser()
        when (result.status) {
            Status.SUCCESS -> onSuccess.invoke()
            Status.ERROR -> {
                Timber.e(result.dktLoginException)
                onError.invoke(result.dktLoginException?.message)
            }
        }
    }
}