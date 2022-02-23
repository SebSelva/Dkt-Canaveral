package com.decathlon.canaveral.intro

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.interactors.Interactors
import com.decathlon.core.user.model.User
import kotlinx.coroutines.launch

class IntroViewModel(
    private val interactors: Interactors,
    private val userConsentManager: UserConsentManager
    ) :BaseViewModel<IntroViewModel.LoginUiState>() {

    init {
        viewModelScope.launch {
            interactors.initLogin.initLogin()
        }
    }

    suspend fun showFirstTimeConsent(activity: FragmentActivity) {
        if (!interactors.userConsent.isSet()) {
            userConsentManager.initConsentDialog(activity)
            interactors.userConsent.validFirst()
        }
    }

    fun initLoginContext(context: Context) = interactors.initLogin.getAuthorizationContext(context)

    fun releaseLoginContext() = interactors.initLogin.releaseAuthorizationContext()

    suspend fun isLogin() = interactors.userLoginState.isLogIn()

    suspend fun requestLogIn() {
        loginState(LoginUiState.LoginInProgress)
        interactors.userLogin(
            onSuccess = {
                viewModelScope.launch { getUserInfo() }
            },
            onError = {
                loginState(LoginUiState.LoginFailed)
            }
        )
    }

    private fun addUser(user: User) {
        viewModelScope.launch {
            interactors.addUser(user)
        }
    }

    private suspend fun getUserInfo() {
        interactors.getUserInfo(
            {
                loginState(LoginUiState.LoginInProgress)
                launchCompleteUserInfo()
            },
            {
                addUser(it)
                loginState(LoginUiState.UserInfoSuccess)
            },
            {
                loginState(LoginUiState.UserInfoFailed)
            }
        )

    }

    private fun launchCompleteUserInfo() {
        viewModelScope.launch {
            interactors.completeUserInfo(
                {
                    getUserInfo()
                    loginState(LoginUiState.UserInfoSuccess)
                },
                { loginState(LoginUiState.UserInfoFailed) },
                { loginState(LoginUiState.UserInfoFailed) }
            )
        }
    }

    private fun loginState(state: LoginUiState) {
        uiState.value = state
    }

    sealed class LoginUiState {
        object LoginInProgress : LoginUiState()
        object LoginFailed : LoginUiState()
        object UserInfoSuccess: LoginUiState()
        object UserInfoFailed : LoginUiState()
    }
}