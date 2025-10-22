package com.eggsa.enois.eggsafeutils.presentation.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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

class EggSafeVi(
    private val context: Context,
    private val callback: EggSafeCallBack
) : WebView(context) {
    init {
//        fitsSystemWindows = true
        val webSettings = settings
        webSettings.apply {
            setSupportMultipleWindows(true)
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            userAgentString = WebSettings.getDefaultUserAgent(context).replace("; wv)", "").replace("Version/4.0 ", "")
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }


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
                }
                else if (URLUtil.isNetworkUrl(link)) {
                    false
                } else if(link.startsWith("intent")){
                    intentStart(link)
                    true
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "This application not found", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                CookieManager.getInstance().flush()
                callback.onFirstPageFinished()
            }


        })

        super.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                callback.onPermissionRequest(request)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams?,
            ): Boolean {
                callback.onShowFileChooser(filePathCallback)
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

    fun fLoad(link: String) {
        super.loadUrl(link)
    }

    fun setUserAgent(userAgent: String) {
        settings.userAgentString = userAgent
    }

    private fun handleCreateWebWindowRequest(resultMsg: Message?) {
        if (resultMsg == null) return
        if (resultMsg.obj != null && resultMsg.obj is WebView.WebViewTransport) {
            val transport = resultMsg.obj as WebView.WebViewTransport
            val windowWebView = EggSafeVi(context, callback)
            transport.webView = windowWebView
            resultMsg.sendToTarget()
            callback.handleCreateWebWindowRequest(windowWebView)
        }
    }

    private fun intentStart(link: String) {
        var scheme = ""
        var token = ""
        val part1 = link.split("#").first()
        val part2 = link.split("#").last()
        token = part1.split("?").last()
        part2.split(";").forEach {
            if (it.startsWith("scheme")) {
                scheme = it.split("=").last()
            }
        }
        val finalUriString = "$scheme://receiveetransfer?$token"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUriString))
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "This application not found", Toast.LENGTH_SHORT).show()
        }
    }

}