package com.eggsa.enois.eggsafeutils.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.eggsa.enois.eggsafeutils.presentation.di.eggSafeModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface AppsFlyerState {
    data object Default : AppsFlyerState
    data class Success(val data: MutableMap<String, Any>?) : AppsFlyerState
    data object Error : AppsFlyerState
}


interface EggSafeAppsApi {
    @Headers("Content-Type: application/json")
    @GET("com.eggsa.enois")
    fun getClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}
private const val EGGSAGE_APP_DEV = "GdvrbFhMMefAu3REGhWn4B"
class EggSafeApp : Application() {

    var isResumed = false

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        eggSafeSetDebufLogger(appsflyer)
        eggSafeMinTimeBetween(appsflyer)


        appsflyer.init(
            EGGSAGE_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    Log.d(EGGSAFE_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = eggSafeGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.getClient(
                                    devkey = EGGSAGE_APP_DEV,
                                    deviceId = eggSafeGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(EGGSAFE_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic") {
                                    safeResume(AppsFlyerState.Error)
                                } else {
                                    safeResume(
                                        AppsFlyerState.Success(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(EGGSAFE_MAIN_TAG, "Error: ${d.message}")
                                safeResume(AppsFlyerState.Error)
                            }
                        }
                    } else {
                        safeResume(AppsFlyerState.Success(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    Log.d(EGGSAFE_MAIN_TAG, "onConversionDataFail: $p0")
                    safeResume(AppsFlyerState.Error)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(EGGSAFE_MAIN_TAG, "onAppOpenAttribution")
//                        safeResume(AppsFlyerState.Error)
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(EGGSAFE_MAIN_TAG, "onAttributionFailure: $p0")
//                        safeResume(AppsFlyerState.Error)
                }
            },
            this
        )

        appsflyer.start(this, EGGSAGE_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(EGGSAFE_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(EGGSAFE_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
                safeResume(AppsFlyerState.Error)
            }
        })


        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@EggSafeApp)
            modules(
                listOf(
                    eggSafeModule
                )
            )
        }

    }

    private fun safeResume(state: AppsFlyerState) {
        if (!isResumed) {
            isResumed = true
            conversionFlow.value = state
        }
    }

    private fun eggSafeGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(EGGSAFE_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun eggSafeSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun eggSafeMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun eggSafeGetApi(url: String, client: OkHttpClient?) : EggSafeAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var inputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val conversionFlow: MutableStateFlow<AppsFlyerState> = MutableStateFlow(AppsFlyerState.Default)
        var EGGSAFE_FIREBASE_PUSH_ID: String? = null
        const val EGGSAFE_MAIN_TAG = "EggSafeMainTag"
    }
}