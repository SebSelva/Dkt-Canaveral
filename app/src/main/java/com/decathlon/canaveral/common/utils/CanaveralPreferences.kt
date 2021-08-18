package com.decathlon.canaveral.common.utils

import android.content.Context
import java.util.*

class CanaveralPreferences(private val context: Context) {

    companion object {
        private const val CANAVERAL_PREFERENCES = "CANAVERAL_PREFERENCES"

        private const val LANGUAGE_APP_KEY = "LANGUAGE_APP_KEY"
    }

    fun saveLocale(locale: Locale) {
        val sharedPreferences = context.getSharedPreferences(CANAVERAL_PREFERENCES, Context.MODE_PRIVATE) ?: return
        with (sharedPreferences.edit()) {
            putString(LANGUAGE_APP_KEY, locale.language)
            apply()
        }
    }

    fun getLocale(): Locale? {
        val sharedPreferences = context.getSharedPreferences(CANAVERAL_PREFERENCES, Context.MODE_PRIVATE) ?: return null
        val language = sharedPreferences.getString(LANGUAGE_APP_KEY, null)
        return language?.let { Locale(language) }
    }
}
