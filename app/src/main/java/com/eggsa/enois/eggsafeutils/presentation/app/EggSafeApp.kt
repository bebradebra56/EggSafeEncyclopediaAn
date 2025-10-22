package com.eggsa.enois.eggsafeutils.presentation.app

import android.app.Application
import android.util.Log
import com.eggsa.enois.eggsafeutils.data.utils.EggSafeAppsflyer
import com.eggsa.enois.eggsafeutils.data.utils.EggSafeSystemService
import com.eggsa.enois.eggsafeutils.presentation.di.eggSafeModule
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


sealed interface AppsFlyerState {
    data object Default : AppsFlyerState
    data class Succes(val data: MutableMap<String, Any>?) : AppsFlyerState
    data object Error : AppsFlyerState
}

class EggSafeApp : Application() {


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@EggSafeApp)
            modules(
                listOf(
                    eggSafeModule
                )
            )
        }
        val appsflyer = EggSafeAppsflyer(this)
        val systemService = EggSafeSystemService(this)
        if (systemService.isOnline()) {
            appsflyer.init { data ->
                conversionFlow.value = data
            }
        }
    }

    companion object {
        val conversionFlow: MutableStateFlow<AppsFlyerState> = MutableStateFlow(AppsFlyerState.Default)
        var EGGSAFE_FIREBASE_PUSH_ID: String? = null
        const val EGGSAFE_MAIN_TAG = "EggSafeMainTag"
    }
}