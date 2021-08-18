package com.decathlon.canaveral.common.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import java.util.*

class ContextUtils(base: Context) : ContextWrapper(base) {

    companion object {

        fun updateLocale(context: Context, localeToSwitchTo: Locale): ContextWrapper {
            Locale.setDefault(localeToSwitchTo)
            return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                updateResourcesLocale(context, localeToSwitchTo)
            } else {
                updateResourcesLocaleLegacy(context, localeToSwitchTo)
            }
        }

        @TargetApi(Build.VERSION_CODES.N_MR1)
        private fun updateResourcesLocale(context: Context, locale: Locale): ContextWrapper {
            val configuration = context.resources.configuration
            configuration.setLayoutDirection(locale)
            configuration.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            return ContextUtils(context.createConfigurationContext(configuration))
        }

        @SuppressWarnings("deprecation")
        private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): ContextWrapper {
            val configuration = context.resources.configuration
            configuration.setLayoutDirection(locale)
            configuration.locale = locale
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            return ContextUtils(context)
        }
    }
}