package com.decathlon.canaveral.intro

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.decathlon.canaveral.BuildConfig
import com.decathlon.core.Constants
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import io.didomi.sdk.Didomi
import io.didomi.sdk.DidomiInitializeParameters
import io.didomi.sdk.events.ConsentChangedEvent
import io.didomi.sdk.events.EventListener
import io.didomi.sdk.events.HideNoticeEvent
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserConsentManager(private val context: Context) {
    private val didomi get() = Didomi.getInstance()

    fun initDidomi() {
        try {
            if (BuildConfig.DEBUG) Didomi.getInstance().setLogLevel(android.util.Log.DEBUG)
            if (!didomi.isInitialized) {
                didomi.initialize(
                    context.applicationContext as Application,
                    DidomiInitializeParameters(
                        Constants.RGPD_KEY
                    )
                )
            }
        } catch (e: Exception) {
            Timber.e("consent init error : ${e.stackTrace}")
        }
    }

    suspend fun initConsentDialog(activity: FragmentActivity): Unit =
        suspendCoroutine { continuation ->
            val eventListener = object : EventListener() {

                override fun hideNotice(event: HideNoticeEvent) {
                    super.hideNotice(event)
                    updateFireBaseConsent()
                    continuation.resume(Unit)
                    // avoid memory leak
                    didomi.removeEventListener(this)
                }
            }

            didomi.apply {
                onReady {
                    if (shouldConsentBeCollected()) {
                        addEventListener(eventListener)
                        setupUI(activity)
                        Timber.i("consent start set")

                    } else {
                        // already preset but still show dialog
                        Timber.i("consent already set")
                        addEventListener(eventListener)
                        forceShowNotice(activity)
                    }
                }
                onError {
                    // we don't want to block the app even error
                    Timber.e("consent init error")
                    continuation.resume(Unit)
                }
            }
            initDidomi()
        }

    // deprecated but only way available in docs, wait for update
    fun hasFirebaseAnalyticsConsent() =
        didomi.enabledVendorIds.contains(FIREBASE_ANALYTICS_VENDOR_ID)

    fun hasFirebaseCrashlyticsConsent() =
        didomi.enabledVendorIds.contains(FIREBASE_CRASHLYTICS_VENDOR_ID)

    fun showConsentDialog(activity: FragmentActivity) {
        val eventListener = object : EventListener() {
            override fun consentChanged(event: ConsentChangedEvent) {
                super.consentChanged(event)
                Timber.d("Firebase Consent analytics = ${hasFirebaseAnalyticsConsent()} crashlytics = ${hasFirebaseCrashlyticsConsent()}")
                updateFireBaseConsent()
                // As Didomi is singleton, we should remove listener to avoid doubloon
                didomi.removeEventListener(this)
            }
        }

        didomi.apply {
            onReady {
                showPreferences(activity)
                addEventListener(eventListener)
            }
        }
    }

    suspend fun didomiControl(activity: FragmentActivity): Unit =
        suspendCoroutine { continuation ->
            val eventListener = object : EventListener() {

                override fun hideNotice(event: HideNoticeEvent) {
                    super.hideNotice(event)
                    updateFireBaseConsent()
                    continuation.resume(Unit)
                    // avoid memory leak
                    didomi.removeEventListener(this)
                }
            }

            didomi.apply {
                onReady {
                    if (isConsentRequired && shouldConsentBeCollected()) {
                        addEventListener(eventListener)
                        forceShowNotice(activity)
                        Timber.w("consent refresh needed")
                    }
                }
                onError {
                    // we don't want to block the app even error
                    Timber.w("consent init error")
                    continuation.resume(Unit)
                }
            }
        }

    private fun updateFireBaseConsent() {
        Firebase.analytics.setAnalyticsCollectionEnabled(hasFirebaseAnalyticsConsent())
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(hasFirebaseCrashlyticsConsent())
    }

    companion object {
        // Those values are custom vendor id in Didomi console, so fixed normally
        const val FIREBASE_ANALYTICS_VENDOR_ID = "c:firebasea-8zY9M4dh"
        const val FIREBASE_CRASHLYTICS_VENDOR_ID = "c:firebasec-cRjZg9jb"
    }

}