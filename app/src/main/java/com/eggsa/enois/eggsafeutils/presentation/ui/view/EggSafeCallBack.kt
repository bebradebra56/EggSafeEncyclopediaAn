package com.eggsa.enois.eggsafeutils.presentation.ui.view


import android.net.Uri
import android.webkit.PermissionRequest
import android.webkit.ValueCallback

interface EggSafeCallBack {
    fun handleCreateWebWindowRequest(EggSafeVi: EggSafeVi)

    fun onPermissionRequest(request: PermissionRequest?)

    fun onShowFileChooser(filePathCallback: ValueCallback<Array<Uri>>?)

    fun onFirstPageFinished()
}