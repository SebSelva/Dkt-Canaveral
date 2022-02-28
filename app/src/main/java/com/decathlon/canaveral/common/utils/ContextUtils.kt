package com.decathlon.canaveral.common.utils

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.LocaleList
import com.decathlon.canaveral.R
import java.util.*

fun updateLocale(context: Context, localeToSwitchTo: Locale): Context {
    Locale.setDefault(localeToSwitchTo)
    return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
        updateResourcesLocale(context, localeToSwitchTo)
    } else {
        updateResourcesLocaleLegacy(context, localeToSwitchTo)
    }
}

@TargetApi(Build.VERSION_CODES.N_MR1)
private fun updateResourcesLocale(context: Context, locale: Locale): Context {
    val configuration = context.resources.configuration
    configuration.setLayoutDirection(locale)
    configuration.setLocale(locale)
    val localeList = LocaleList(locale)
    LocaleList.setDefault(localeList)
    configuration.setLocales(localeList)
    return context.createConfigurationContext(configuration)
}

@SuppressWarnings("deprecation")
private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
    val configuration = context.resources.configuration
    configuration.setLayoutDirection(locale)
    configuration.locale = locale
    context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    return context
}

fun getLanguageNameFromCode(context: Context, code: String): String {
    val languages = context.resources.getStringArray(R.array.languages_trad)
    val languageCodes = context.resources.getStringArray(R.array.languages_code)
    return languages[languageCodes.indexOf(code)]
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    return capabilities?.let {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } == true
}