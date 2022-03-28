package com.decathlon.core

import com.decathlon.decathlonlogin.Environment

class Constants{

    companion object {
        fun getDktEnv() = Environment.PREPROD

        const val IDENTITY_API_KEY = "930a8772-d83f-43da-8243-430863f917b7"
        const val AUTH_CLIENT_ID = "93a46726-718f-48d4-9ff1-23811b1d0626"
        const val AUTH_REDIRECT_SCHEME = "homedartsclub"
        const val AUTH_REDIRECT_HOST_LOGIN = "oauth"
        const val AUTH_REDIRECT_HOST_LOGOUT = "logout/"

        const val RGPD_KEY = "0c3a4d8d-c6dc-4e61-83d3-1d4c4b99cc2e"

        const val STD_KEY = "ceeeca02-3b6d-4ebb-b88b-2a4273078da4"
        const val STD_BASE_URL = "https://api-global.preprod.decathlon.net/sportstrackingdata/"
    }
}