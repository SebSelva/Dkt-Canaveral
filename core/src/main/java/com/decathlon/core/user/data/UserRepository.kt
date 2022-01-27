package com.decathlon.core.user.data

import android.content.Context
import com.decathlon.core.Constants
import com.decathlon.core.user.common.AuthResource
import com.decathlon.core.user.data.source.UserDataSource
import com.decathlon.core.user.data.source.network.STDServices
import com.decathlon.core.user.model.User
import com.decathlon.decathlonlogin.Configuration
import com.decathlon.decathlonlogin.DktLoginManager
import com.decathlon.decathlonlogin.DktLoginState
import com.decathlon.decathlonlogin.api.member.model.Identity
import com.decathlon.decathlonlogin.exception.DktLoginException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepository(
    private val dataSource: UserDataSource,
    private val dktLoginManager: DktLoginManager,
    private val stdServices: STDServices
    )
{

    suspend fun addUser(user: User) = withContext(Dispatchers.IO) {
        dataSource.insertUser(user)
    }

    suspend fun removeUser(user: User) = withContext(Dispatchers.IO) {
        dataSource.removeUser(user)
    }

    suspend fun getUsers() = withContext(Dispatchers.IO) {
        dataSource.getUsers()
    }

    suspend fun getMainUser() = withContext(Dispatchers.IO) {
        dataSource.getMainUser()
    }

    suspend fun updateUser(user: User) =  withContext(Dispatchers.IO) {
        dataSource.updateUser(user)
    }

    fun initLogin() {
        dktLoginManager.init(
            Configuration(
                Constants.getDktEnv(),
                Constants.AUTH_CLIENT_ID,
                Constants.AUTH_REDIRECT_SCHEME,
                Constants.AUTH_REDIRECT_HOST_LOGIN,
                Constants.AUTH_REDIRECT_HOST_LOGOUT,
                Constants.IDENTITY_API_KEY
            )
        )
    }

    fun getAuthorizationContext(context: Context) {
        dktLoginManager.initAuthorizationContext(context)
    }

    fun releaseAuthorizationContext() {
        dktLoginManager.releaseAuthorizationContext()
    }

    suspend fun logInUser(): AuthResource<DktLoginState> = suspendCoroutine { continuation ->
        dktLoginManager.login { authState: DktLoginState, error: DktLoginException? ->
            if (error == null) {
                continuation.resume(AuthResource.success(data = authState))
            } else {
                continuation.resume(AuthResource.error(dktLoginException = error, data = authState))
            }
        }
    }

    suspend fun getSTDId(accessToken: String?): String {
        try {
            val accountInfo = stdServices.getAccountInfo(
                Constants.STD_KEY,
                "Bearer $accessToken"
            )
            return accountInfo.id
        } catch (e: Exception) {
            Timber.e("STD get user id error: $e")
        }
        return ""
    }

    suspend fun logOutUser(): AuthResource<DktLoginState> = suspendCoroutine { continuation ->
        dktLoginManager.logout { authState: DktLoginState, error: DktLoginException? ->
            if (error == null) {
                continuation.resume(AuthResource.success(data = authState))
            } else {
                continuation.resume(AuthResource.error(dktLoginException = error, data = authState))
            }
        }
    }

    suspend fun getUserIdentity(): AuthResource<User?> =
        suspendCoroutine { continuation ->
            try {
                val identity = dktLoginManager.getIdentity().toObservable().blockingSingle()
                if (isCompleted(identity)) {
                    continuation.resume(
                        AuthResource.success(
                            User(
                                0,
                                "",
                                identity.firstname!!,
                                identity.lastname!!,
                                null,
                                null,
                                true
                            )
                        )
                    )
                } else {
                    continuation.resume(AuthResource.success(null))
                }
            }
            catch (e: Exception) {
                Timber.e(e)
                continuation.resume(AuthResource.error(null, null))
            }
        }

    suspend fun launchIdentityCompletion(): AuthResource<Boolean> =
        suspendCoroutine { continuation ->
            dktLoginManager.launchIdentityCompletion { completed: Boolean, error: DktLoginException? ->
                if (error == null) {
                    continuation.resume(AuthResource.success(data = completed))
                } else {
                    continuation.resume(
                        AuthResource.error(
                            data = completed,
                            dktLoginException = error
                        )
                    )
                }
            }
        }

    fun refreshToken() = dktLoginManager.refresh()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    suspend fun isUserRegistered() = getMainUser() != null

    private fun isCompleted(identity: Identity): Boolean =
        !identity.firstname.isNullOrEmpty() && !identity.lastname.isNullOrEmpty()
            && identity.birthdate != null && identity.gender == Identity.Gender.UNSPECIFIED

    fun isLogIn() = dktLoginManager.getState().isAuthorized

    fun needsTokenRefresh() = dktLoginManager.getState().needsTokenRefresh

    fun getAccessToken() = dktLoginManager.getState().accessToken

    companion object {
        fun isLoginError(dktLoginException: DktLoginException?): Boolean = when (dktLoginException) {
            is DktLoginException.UnauthorizedClientException,
            is DktLoginException.InvalidClientException,
            is DktLoginException.InvalidGrantException,
            is DktLoginException.ConnectionException,
            is DktLoginException.ClientErrorException -> true
            else -> false
        }
    }

}