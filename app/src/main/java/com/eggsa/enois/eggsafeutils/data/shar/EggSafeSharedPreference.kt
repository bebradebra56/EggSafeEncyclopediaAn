package com.eggsa.enois.eggsafeutils.data.shar

import android.content.Context
import androidx.core.content.edit

class EggSafeSharedPreference(context: Context) {
    private val prefs = context.getSharedPreferences("egg_safe_app_prefs", Context.MODE_PRIVATE)

    var savedUrl: String
        get() = prefs.getString(EGGSAFE_SAVED_URL, "") ?: ""
        set(value) = prefs.edit { putString(EGGSAFE_SAVED_URL, value) }

    var expired : Long
        get() = prefs.getLong(EFFSAFE_EXPIRED, 0L)
        set(value) = prefs.edit { putLong(EFFSAFE_EXPIRED, value) }

    var appState: Int
        get() = prefs.getInt(EGGSAGE_APPLICATION_STATE, 0)
        set(value) = prefs.edit { putInt(EGGSAGE_APPLICATION_STATE, value) }

    var notificationRequest: Long
        get() = prefs.getLong(EGGSAFE_NOTIFICAITON_REQUEST, 0L)
        set(value) = prefs.edit { putLong(EGGSAFE_NOTIFICAITON_REQUEST, value) }

    var notificationRequestedBefore: Boolean
        get() = prefs.getBoolean(EGGSAFE_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = prefs.edit { putBoolean(EGGSAFE_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val EGGSAFE_SAVED_URL = "eggSafeSavedUrl"
        private const val EFFSAFE_EXPIRED = "eggSafeExpired"
        private const val EGGSAGE_APPLICATION_STATE = "eggSafeApplicationState"
        private const val EGGSAFE_NOTIFICAITON_REQUEST = "eggSafeNotificationRequest"
        private const val EGGSAFE_NOTIFICATION_REQUEST_BEFORE = "notificationRequestedBefore"
    }
}