package com.eggsa.enois.eggsafeutils.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class EggSafeSystemService(private val context: Context) {
//    fun getSimState(): Boolean {
//        val telManager =
//            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        val simLocation = telManager.simState
//        return when(simLocation) {
//            TelephonyManager.SIM_STATE_ABSENT -> false
//            TelephonyManager.SIM_STATE_UNKNOWN -> false
//            else -> true
//        }
//    }

    suspend fun getGaid() : String  = withContext(Dispatchers.IO){
        val gaid = AdvertisingIdClient.getAdvertisingIdInfo(context).id ?: "00000000-0000-0000-0000-000000000000"
        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Gaid: $gaid")
        return@withContext gaid
    }

    fun getAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: ""
        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    fun getLocale() : String {
        return  Locale.getDefault().language
    }

    fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return true
            }
        }
        return false
    }

}