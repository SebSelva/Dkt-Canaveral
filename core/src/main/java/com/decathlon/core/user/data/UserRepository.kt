package com.decathlon.core.user.data

import android.content.Context
import com.decathlon.core.Constants
import com.decathlon.core.user.common.AuthResource
import com.decathlon.core.user.data.source.UserDataSource
import com.decathlon.core.user.data.source.datastore.AccountPreference
import com.decathlon.core.user.model.User
import com.decathlon.decathlonlogin.Configuration
import com.decathlon.decathlonlogin.DktLoginManager
import com.decathlon.decathlonlogin.DktLoginState
import com.decathlon.decathlonlogin.api.member.model.Identity
import com.decathlon.decathlonlogin.exception.DktLoginException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepository(
    private val dataSource: UserDataSource,
    private val dktLoginManager: DktLoginManager,
    private val accountPreference: AccountPreference
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

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        dataSource.updateUser(user)
    }

    suspend fun validFirstConsent() = withContext(Dispatchers.IO) {
        accountPreference.validConsent()
    }

    suspend fun isConsentSet(): Boolean = withContext(Dispatchers.IO) {
        accountPreference.isConsentSet().first()
    }

    init {
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

    suspend fun refreshToken(): AuthResource<Boolean> =
        suspendCoroutine { continuation ->
            dktLoginManager.refresh().subscribe { onSuccess, onError ->
                if (onError == null) {
                    onSuccess.isAuthorized.let { continuation.resume(AuthResource.success(it)) }
                } else {
                    continuation.resumeWithException(onError)
                }
            }.dispose()
        }

    suspend fun isUserRegistered() = getMainUser() != null

    private fun isCompleted(identity: Identity): Boolean =
        !identity.firstname.isNullOrEmpty() && !identity.lastname.isNullOrEmpty()

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