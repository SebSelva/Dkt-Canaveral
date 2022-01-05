package com.decathlon.canaveral.common.utils

import android.os.Bundle
import com.decathlon.canaveral.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class FirebaseManager {

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    init {
        val parameters = Bundle().apply {
            this.putString("app_version", BuildConfig.VERSION_NAME)
        }
        firebaseAnalytics.setDefaultEventParameters(parameters)
    }

    fun logGameStartEvent(gameType: String, variant: String, playerNumber: Int, scoringMode: String) {
        val parameters = Bundle().apply {
            this.putString(FirebaseEventNames.GAME_START_GAME_TYPE, gameType)
            this.putString(FirebaseEventNames.GAME_START_GAME_VARIANT, variant)
            this.putInt(FirebaseEventNames.GAME_START_PLAYER_NUMBER, playerNumber)
            this.putString(FirebaseEventNames.GAME_START_SCORING_METHOD, scoringMode)
        }
        firebaseAnalytics.logEvent(FirebaseEventNames.GAME_START, parameters)
    }
}