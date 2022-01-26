package com.decathlon.canaveral.common.interactors.user

import com.decathlon.core.user.common.Status
import com.decathlon.core.user.data.UserRepository
import com.decathlon.core.user.model.User

class GetUserInfo(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userInfoNotComplete: () -> Unit,
        onSuccess: (User) -> Unit,
        onError: () -> Unit
    ) {
        val result = userRepository.getUserIdentity()
        when (result.status) {
            Status.SUCCESS -> {
                if (result.data != null) onSuccess(result.data!!) else userInfoNotComplete()
            }
            Status.ERROR -> onError()
        }
    }
}