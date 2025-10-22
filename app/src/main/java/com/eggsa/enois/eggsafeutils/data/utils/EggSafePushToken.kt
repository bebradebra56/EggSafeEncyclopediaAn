package com.eggsa.enois.eggsafeutils.data.utils

import android.util.Log
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EggSafePushToken {

    suspend fun getToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}