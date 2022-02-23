package com.decathlon.canaveral.common.interactors.user

import com.decathlon.core.user.data.UserRepository

class UserConsent(private val userRepository: UserRepository) {

    suspend fun isSet() = userRepository.isConsentSet()
    suspend fun validFirst() = userRepository.validFirstConsent()
}