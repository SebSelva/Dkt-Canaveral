package com.decathlon.core

import com.decathlon.decathlonlogin.Environment

class Constants{

    companion object {
        fun getDktEnv() = Environment.PROD

        const val IDENTITY_API_KEY = ""
        const val AUTH_CLIENT_ID = ""
        const val AUTH_REDIRECT_SCHEME = "homedartsclub"
        const val AUTH_REDIRECT_HOST_LOGIN = "oauth"
        const val AUTH_REDIRECT_HOST_LOGOUT = "logout"

        const val RGPD_KEY = "0c3a4d8d-c6dc-4e61-83d3-1d4c4b99cc2e"
        
        const val STD_KEY = "f5e29200-ddf6-40d5-a35c-4a37626b8969"
        const val STD_BASE_URL = "https://api-global.decathlon.net/sportstrackingdata/"
    }
}