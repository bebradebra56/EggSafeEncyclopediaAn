package com.eggsa.enois.eggsafeutils.presentation.ui.view

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EggSafeDataStore : ViewModel(){
    val eggSafeViList: MutableList<EggSafeVi> = mutableListOf()
    private val _isFirstFinishPage: MutableStateFlow<Boolean> = MutableStateFlow(true)
//    val isFirstFinishPage: StateFlow<Boolean> = _isFirstFinishPage.asStateFlow()

    fun setIsFirstFinishPage() {
        _isFirstFinishPage.value = false
    }
}