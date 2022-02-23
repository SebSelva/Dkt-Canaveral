package com.decathlon.canaveral.common.utils

import android.content.Context
import com.decathlon.canaveral.R
import java.util.*

class LocaleUtils(val context: Context, private val preferences: CanaveralPreferences) {

    init {
        val availableLocales = context.resources.getStringArray(R.array.languages_code_available)
        val currentLocale = Locale.getDefault()
        val locale = getCurrentLocale()
        if (locale == null) {
            if (!availableLocales.contains(currentLocale.language)) {
                // Set available locale in default value : en
                setCurrentLocale(context, Locale("en"))
            } else {
                setCurrentLocale(context, currentLocale)
            }
        } else if (currentLocale.language != locale.language) {
            setCurrentLocale(context, locale)
        }
    }

    fun setCurrentLocale(context: Context, locale: Locale) {
        updateLocale(context, locale)
        context.resources.updateConfiguration(context.resources.configuration, context.resources.displayMetrics)
        preferences.saveLocale(locale)
    }

    fun getCurrentLocale(): Locale? {
        return preferences.getLocale()
    }
}