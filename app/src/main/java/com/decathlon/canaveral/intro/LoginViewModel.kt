package com.decathlon.canaveral.intro

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.interactors.Interactors
import com.decathlon.core.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(
    private val interactors: Interactors,
    private val userConsentManager: UserConsentManager
    ) :BaseViewModel<LoginViewModel.LoginUiState>() {

    suspend fun showFirstTimeConsent(activity: FragmentActivity) {
        if (!interactors.userConsent.isSet()) {
            userConsentManager.initConsentDialog(activity)
            interactors.userConsent.validFirst()
        }
    }

    fun initLoginContext(context: Context) = interactors.initLogin.getAuthorizationContext(context)

    fun releaseLoginContext() = interactors.initLogin.releaseAuthorizationContext()

    fun isLogin() = interactors.userLoginState.isLogIn()

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
            interactors.userActions.addUser(user)
        }
    }

    suspend fun getMainUser(): Flow<User?> {
        return interactors.userActions.getMainUser()
    }

    suspend fun getUsers(): LiveData<List<User>> {
        return interactors.userActions.getUsers().asLiveData()
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
                { getUserInfo() },
                { loginState(LoginUiState.UserInfoIncomplete) },
                { loginState(LoginUiState.UserInfoFailed) }
            )
        }
    }

    fun requestLogout() {
        viewModelScope.launch {
            interactors.userLogout.invoke(
                {
                    interactors.userActions.getMainUser().first()
                        ?.let {
                            interactors.userActions.removeUser(it)
                            interactors.stdActions.deleteAllUserData()
                        }
                    loginState(LoginUiState.LogoutSuccess)
                },
                { loginState(LoginUiState.LogoutFailed) }
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
        object UserInfoIncomplete : LoginUiState()
        object UserInfoFailed : LoginUiState()
        object LogoutSuccess : LoginUiState()
        object LogoutFailed : LoginUiState()
    }
}