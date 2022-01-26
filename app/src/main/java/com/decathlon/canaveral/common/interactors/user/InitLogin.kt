package com.decathlon.canaveral.common.interactors.user

import android.content.Context
import com.decathlon.core.user.data.UserRepository

class InitLogin(private val userRepository: UserRepository) {

    fun initLogin() = userRepository.initLogin()
    fun getAuthorizationContext(context: Context) = userRepository.getAuthorizationContext(context)
    fun releaseAuthorizationContext() = userRepository.releaseAuthorizationContext()

}