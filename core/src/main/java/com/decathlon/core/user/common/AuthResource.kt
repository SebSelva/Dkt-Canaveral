package com.decathlon.core.user.common

import com.decathlon.decathlonlogin.exception.DktLoginException

data class AuthResource<out T>(
    val status: Status,
    val data: T,
    val dktLoginException: DktLoginException?
) {
    companion object {
        fun <T> success(data: T): AuthResource<T> {
            return AuthResource(Status.SUCCESS, data, null)
        }

        fun <T> error(dktLoginException: DktLoginException?, data: T): AuthResource<T> {
            return AuthResource(Status.ERROR, data, dktLoginException)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
}

