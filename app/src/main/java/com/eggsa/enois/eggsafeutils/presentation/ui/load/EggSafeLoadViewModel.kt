package com.eggsa.enois.eggsafeutils.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eggsa.enois.eggsafeutils.data.shar.EggSafeSharedPreference
import com.eggsa.enois.eggsafeutils.data.utils.EggSafeSystemService
import com.eggsa.enois.eggsafeutils.domain.usecases.EggSafeGetAllUseCase
import com.eggsa.enois.eggsafeutils.presentation.app.AppsFlyerState
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EggSafeLoadViewModel(
    private val getAllUseCase: EggSafeGetAllUseCase,
    private val eggSafeSharedPreference: EggSafeSharedPreference,
    private val eggSafeSystemService: EggSafeSystemService
) : ViewModel() {

    private val _eggSafeHomeScreenState: MutableStateFlow<EggSafeHomeScreenState> =
        MutableStateFlow(EggSafeHomeScreenState.EggSafeLoading)
    val crasherHomeScreenState = _eggSafeHomeScreenState.asStateFlow()

    private var getApps = false


    init {
        viewModelScope.launch {
            when (eggSafeSharedPreference.appState) {
                0 -> {
                    if (eggSafeSystemService.isOnline()) {
                        EggSafeApp.conversionFlow.collect {
                            when(it) {
                                AppsFlyerState.Default -> {}
                                AppsFlyerState.Error -> {
                                    eggSafeSharedPreference.appState = 2
                                    _eggSafeHomeScreenState.value = EggSafeHomeScreenState.EggSafeError
                                    getApps = true
                                }
                                is AppsFlyerState.Succes -> {
                                    if (!getApps) {
                                        getData(it.data)
                                        getApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _eggSafeHomeScreenState.value = EggSafeHomeScreenState.NotInternet
                    }
                }
                1 -> {
                    if (eggSafeSystemService.isOnline()) {
                        if (EggSafeApp.EGGSAFE_FIREBASE_PUSH_ID != null) {
                            _eggSafeHomeScreenState.value = EggSafeHomeScreenState.Success(EggSafeApp.EGGSAFE_FIREBASE_PUSH_ID.toString())
                        } else if (System.currentTimeMillis() / 1000 > eggSafeSharedPreference.expired) {
                            Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Current time more then expired, repeat request")
                            EggSafeApp.conversionFlow.collect {
                                when(it) {
                                    AppsFlyerState.Default -> {}
                                    AppsFlyerState.Error -> {
                                        _eggSafeHomeScreenState.value = EggSafeHomeScreenState.Success(eggSafeSharedPreference.savedUrl)
                                        getApps = true
                                    }
                                    is AppsFlyerState.Succes -> {
                                        if (!getApps) {
                                            getData(it.data)
                                            getApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Current time less then expired, use saved url")
                            _eggSafeHomeScreenState.value = EggSafeHomeScreenState.Success(eggSafeSharedPreference.savedUrl)
                        }
                    } else {
                        _eggSafeHomeScreenState.value = EggSafeHomeScreenState.NotInternet
                    }
                }
                2 -> {
                    _eggSafeHomeScreenState.value = EggSafeHomeScreenState.EggSafeError
                }
            }
        }
    }


    private suspend fun getData(conversation: MutableMap<String, Any>?) {
        val data = getAllUseCase.invoke(conversation)
        if (eggSafeSharedPreference.appState == 0) {
            if (data == null) {
                eggSafeSharedPreference.appState = 2
                _eggSafeHomeScreenState.value = EggSafeHomeScreenState.EggSafeError
            } else {
                eggSafeSharedPreference.appState = 1
                eggSafeSharedPreference.apply {
                    expired = data.expires
                    savedUrl = data.url
                }
                _eggSafeHomeScreenState.value = EggSafeHomeScreenState.Success(data.url)
            }
        } else  {
            if (data == null) {
                _eggSafeHomeScreenState.value = EggSafeHomeScreenState.Success(eggSafeSharedPreference.savedUrl)
            } else {
                eggSafeSharedPreference.apply {
                    expired = data.expires
                    savedUrl = data.url
                }
                _eggSafeHomeScreenState.value = EggSafeHomeScreenState.Success(data.url)
            }
        }
    }


    sealed class EggSafeHomeScreenState {
        data object EggSafeLoading : EggSafeHomeScreenState()
        data object EggSafeError : EggSafeHomeScreenState()
        data class Success(val data: String) : EggSafeHomeScreenState()
        data object NotInternet: EggSafeHomeScreenState()
    }
}