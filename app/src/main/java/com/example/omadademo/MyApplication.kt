package com.example.omadademo

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber logging
        if (BuildConfig.DEBUG) {
            // Debug tree for development
            Timber.plant(Timber.DebugTree())
        } else {
            // Production tree - log errors to Firebase Crashlytics
            Timber.plant(CrashReportingTree())
        }

        Timber.d("OmadaDemo app started")
    }
}

/**
 * Production logging tree that sends errors to crash reporting service.
 *
 * Features:
 * - Filters verbose and debug logs in production
 * - Records exceptions to Firebase Crashlytics (when integrated)
 * - Provides structured error logging for debugging
 * - Maintains compatibility with standard Android logging
 *
 * To integrate Firebase Crashlytics:
 * 1. Add Firebase dependency: implementation("com.google.firebase:firebase-crashlytics-ktx")
 * 2. Initialize Firebase in MyApplication.onCreate()
 * 3. Uncomment the FirebaseCrashlytics.getInstance().recordException(t) line below
 */
private class CrashReportingTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Don't log verbose or debug messages in production
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        // Send errors to crash reporting service (Firebase Crashlytics, Sentry, etc.)
        if (t != null && priority == Log.ERROR) {
            // TODO: Uncomment when Firebase Crashlytics is integrated
            // FirebaseCrashlytics.getInstance().recordException(t)

            // Alternative: Send to other crash reporting services
            // - Sentry.captureException(t)
            // - Bugsnag.notifyError(t)
            // - RollbarNotifier.error(t)
        }

        // Log warnings and info messages for monitoring
        if (priority >= Log.WARN) {
            logWithContext(priority, tag, message, t)
        }
    }

    /**
     * Logs message with additional context for better debugging.
     */
    private fun logWithContext(priority: Int, tag: String?, message: String, t: Throwable?) {
        val timestamp = System.currentTimeMillis()
        val contextMessage = "[$timestamp] $message"

        when (priority) {
            Log.ERROR -> Log.e(tag, contextMessage, t)
            Log.WARN -> Log.w(tag, contextMessage, t)
            Log.INFO -> Log.i(tag, contextMessage)
        }
    }
}
