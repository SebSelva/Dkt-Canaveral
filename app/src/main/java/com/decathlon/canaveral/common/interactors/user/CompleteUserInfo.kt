package com.decathlon.canaveral.common.interactors.user

import com.decathlon.core.user.common.AuthResource
import com.decathlon.core.user.common.Status
import com.decathlon.core.user.data.UserRepository

class CompleteUserInfo(
    @PublishedApi
    internal val userRepository: UserRepository
) {
    suspend inline operator fun invoke(
        onUserInfoCompleted: () -> Unit,
        onUserInfoIncomplete: () -> Unit,
        onUserInfoCompleteError: (String) -> Unit
    ) {
        val result: AuthResource<Boolean> = userRepository.launchIdentityCompletion()
        when (result.status) {
            Status.SUCCESS -> if (result.data) onUserInfoCompleted() else onUserInfoIncomplete()
            Status.ERROR -> onUserInfoCompleteError(result.dktLoginException?.message ?: "")
        }
    }

}