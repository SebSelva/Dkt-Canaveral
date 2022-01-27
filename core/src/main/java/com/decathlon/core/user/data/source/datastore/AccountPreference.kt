package com.decathlon.core.user.data.source.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException

class AccountPreference(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = ACCOUNT_GLOBAL_KEY
    )

    suspend fun validConsent() {
        try {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.CONSENT_KEY] = true
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    suspend fun isConsentSet(): Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.CONSENT_KEY] ?: false
        }

    private object PreferencesKeys {
        val CONSENT_KEY = booleanPreferencesKey(ACCOUNT_CONSENT_KEY)
    }

    companion object {
        private const val ACCOUNT_GLOBAL_KEY = "ACCOUNT_GLOBAL_KEY"
        private const val ACCOUNT_CONSENT_KEY = "ACCOUNT_PREF_KEY"
    }
}