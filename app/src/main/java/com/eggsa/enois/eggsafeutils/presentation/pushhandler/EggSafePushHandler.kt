package com.eggsa.enois.eggsafeutils.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp

class EggSafePushHandler() {
    fun handlePush(extras: Bundle?) {
        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = chikenCoopBundleToMap(extras)
            Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    EggSafeApp.EGGSAFE_FIREBASE_PUSH_ID = map["url"]
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Push data no!")
        }
    }

    private fun chikenCoopBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}