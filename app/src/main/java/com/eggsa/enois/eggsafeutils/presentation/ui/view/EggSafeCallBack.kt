package com.eggsa.enois.eggsafeutils.presentation.ui.view


import android.net.Uri
import android.webkit.PermissionRequest
import android.webkit.ValueCallback

interface EggSafeCallBack {
    fun eggSafeHandleCreateWebWindowRequest(eggSafeVi: EggSafeVi)

    fun eggSafeOnPermissionRequest(eggSafeRequest: PermissionRequest?)


    fun eggSafeOnFirstPageFinished()
}