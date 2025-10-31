package com.eggsa.enois.eggsafeutils.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class EggSafeDataStore : ViewModel(){
    val eggSafeViList: MutableList<EggSafeVi> = mutableListOf()
    private val _eggSafeIsFirstFinishPage: MutableStateFlow<Boolean> = MutableStateFlow(true)
    var isFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var containerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var eggSafeView: EggSafeVi

    fun eggSafeSetIsFirstFinishPage() {
        _eggSafeIsFirstFinishPage.value = false
    }
}