package com.eggsa.enois.eggsafeutils.data.utils

import android.content.Context
import android.os.Looper
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.eggsa.enois.eggsafeutils.presentation.app.AppsFlyerState
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query


private const val EGGSAGE_APP_DEV = "GdvrbFhMMefAu3REGhWn4B"

class EggSafeAppsflyer(private val context: Context) {
//    private val ktorClient = HttpClient(OkHttp) {
//        install(ContentNegotiation) {
//            json(Json {
//                ignoreUnknownKeys = true
//            })
//        }
//        install(HttpTimeout) {
//            connectTimeoutMillis = 30000
//            socketTimeoutMillis = 30000
//            requestTimeoutMillis = 30000
//        }
////        install(DefaultRequest) {
////            header("User-Agent", System.getProperty("http.agent") ?: "")
////        }
//
//    }


    fun init(
        callback: (AppsFlyerState) -> Unit
//         callback: (MutableMap<String, Any>?) -> Unit
    ) {
        val appsflyer = AppsFlyerLib.getInstance()
        setDebufLogger(appsflyer)
        minTimeBetween(appsflyer)
        appsflyer.init(
            EGGSAGE_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    Looper.prepare()
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "AppsFlyer: onConversionDataSuccess")
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "AppsFlyer: $p0")
                    Log.d(
                        EggSafeApp.EGGSAFE_MAIN_TAG,
                        "AppsFlyer: af_status: ${p0?.get("af_status")}"
                    )
//                    callback(AppsFlyerState.Succes(p0))
                    if (p0?.get("af_status") == "Organic") {
                        val corouteScope = CoroutineScope(Dispatchers.IO)
                        corouteScope.launch {
                            try {
                                delay(5000)
                                val api = getApi("https://gcdsdk.appsflyer.com/install_data/v4.0/", null)
                                val request = api.getClient(
                                    devkey = EGGSAGE_APP_DEV,
                                    deviceId = getAppsflyerId()
                                )
                                val response = request.awaitResponse()
                                Log.d(
                                    EggSafeApp.EGGSAFE_MAIN_TAG,
                                    "AppsFlyer: Conversion after 5 seconds: ${response.body()}"
                                )
                                if (response.body()?.get("af_status") == "Organic") {
                                    callback(AppsFlyerState.Error)
                                } else {
                                    callback(AppsFlyerState.Succes(response.body()))
                                }
                            } catch (e: Exception) {
                                Log.d(
                                    EggSafeApp.EGGSAFE_MAIN_TAG,
                                    "AppsFlyer: ${e.message}"
                                )
                            }
                        }
                    } else {
                        callback(AppsFlyerState.Succes(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "AppsFlyer: onConversionDataFail: $p0")
                    callback(AppsFlyerState.Error)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "AppsFlyer: onAppOpenAttribution")
                    callback(AppsFlyerState.Error)
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "AppsFlyer: onAttributionFailure: $p0")
                    callback(AppsFlyerState.Error)
                }
            },
            context.applicationContext
        )
        appsflyer.start(context, EGGSAGE_APP_DEV, object : AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "AppsFlyer: Start is Success")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "AppsFlyer: Start is Error")
                Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "AppsFlyer: Error code: $p0, error message: $p1")
                callback(AppsFlyerState.Error)
            }

        })
    }

    private fun getAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: ""
        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun setDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun minTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun getApi(url: String, client: OkHttpClient?) : EggSafeAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

}


interface EggSafeAppsApi {
    @Headers("Content-Type: application/json")
    @GET("com.eggsa.enois")
    fun getClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}