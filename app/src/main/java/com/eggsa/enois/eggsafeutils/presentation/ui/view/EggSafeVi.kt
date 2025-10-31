package com.eggsa.enois.eggsafeutils.presentation.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp

class EggSafeVi(
    private val eggSafeContext: Context,
    private val eggSafeCallback: EggSafeCallBack,
    private val eggSafeWindow: Window
) : WebView(eggSafeContext) {

    private var fileChooserHandler: ((ValueCallback<Array<Uri>>?) -> Unit)? = null

    fun setFileChooserHandler(handler: (ValueCallback<Array<Uri>>?) -> Unit) {
        this.fileChooserHandler = handler
    }

    init {
        val webSettings = settings
        webSettings.apply {
            setSupportMultipleWindows(true)
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            userAgentString = WebSettings.getDefaultUserAgent(eggSafeContext).replace("; wv)", "")
                .replace("Version/4.0 ", "")
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        isNestedScrollingEnabled = true


        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        super.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                val link = request?.url?.toString() ?: ""

                return if (request?.isRedirect == true) {
                    view?.loadUrl(request?.url.toString())
                    true
                } else if (URLUtil.isNetworkUrl(link)) {
                    false
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    try {
                        eggSafeContext.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(eggSafeContext, "This application not found", Toast.LENGTH_SHORT)
                            .show()
                    }
                    true
                }
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                CookieManager.getInstance().flush()
                eggSafeCallback.eggSafeOnFirstPageFinished()
                if (url?.contains("ninecasino") == true) {
                    EggSafeApp.inputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "onPageFinished : ${EggSafeApp.inputMode}")
                    eggSafeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                } else {
                    EggSafeApp.inputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "onPageFinished : ${EggSafeApp.inputMode}")
                    eggSafeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }


        })

        super.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                eggSafeCallback.eggSafeOnPermissionRequest(request)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams?,
            ): Boolean {
                fileChooserHandler?.invoke(filePathCallback)
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                handleCreateWebWindowRequest(resultMsg)
                return true
            }
        })
    }


    fun eggSafeFLoad(link: String) {
        super.loadUrl(link)
    }

    fun setUserAgent(userAgent: String) {
        settings.userAgentString = userAgent
    }

    private fun handleCreateWebWindowRequest(resultMsg: Message?) {
        if (resultMsg == null) return
        if (resultMsg.obj != null && resultMsg.obj is WebView.WebViewTransport) {
            val transport = resultMsg.obj as WebView.WebViewTransport
            val windowWebView = EggSafeVi(eggSafeContext, eggSafeCallback, eggSafeWindow)
            transport.webView = windowWebView
            resultMsg.sendToTarget()
            eggSafeCallback.eggSafeHandleCreateWebWindowRequest(windowWebView)
        }
    }


}