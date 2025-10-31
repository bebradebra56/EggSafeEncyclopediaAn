package com.eggsa.enois.eggsafeutils.presentation.ui.view

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.eggsa.enois.EggSafeActivity
import com.eggsa.enois.R
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp
import com.eggsa.enois.eggsafeutils.presentation.ui.load.EggSafeLoadFragment
import org.koin.android.ext.android.inject

class EggSafeV : Fragment(){

    private lateinit var eggSafePhoto: Uri
    private var eggSafeFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val eggSafeTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        eggSafeFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        eggSafeFilePathFromChrome = null
    }

    private val eggSafeTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            eggSafeFilePathFromChrome?.onReceiveValue(arrayOf(eggSafePhoto))
            eggSafeFilePathFromChrome = null
        } else {
            eggSafeFilePathFromChrome?.onReceiveValue(null)
            eggSafeFilePathFromChrome = null
        }
    }

    private val eggSafeDataStore by activityViewModels<EggSafeDataStore>()


    private val eggSafeViFun by inject<EggSafeViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (eggSafeDataStore.eggSafeView.canGoBack()) {
                        eggSafeDataStore.eggSafeView.goBack()
                        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "WebView can go back")
                    } else if (eggSafeDataStore.eggSafeViList.size > 1) {
                        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "WebView can`t go back")
                        eggSafeDataStore.eggSafeViList.removeAt(eggSafeDataStore.eggSafeViList.lastIndex)
                        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "WebView list size ${eggSafeDataStore.eggSafeViList.size}")
                        eggSafeDataStore.eggSafeView.destroy()
                        val previousWebView = eggSafeDataStore.eggSafeViList.last()
                        attachWebViewToContainer(previousWebView)
                        eggSafeDataStore.eggSafeView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (eggSafeDataStore.isFirstCreate) {
            eggSafeDataStore.isFirstCreate = false
            eggSafeDataStore.containerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return eggSafeDataStore.containerView
        } else {
            return eggSafeDataStore.containerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "onViewCreated")
        if (eggSafeDataStore.eggSafeViList.isEmpty()) {
            eggSafeDataStore.eggSafeView = EggSafeVi(requireContext(), object :
                EggSafeCallBack {
                override fun eggSafeHandleCreateWebWindowRequest(eggSafeVi: EggSafeVi) {
                    eggSafeDataStore.eggSafeViList.add(eggSafeVi)
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "WebView list size = ${eggSafeDataStore.eggSafeViList.size}")
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "CreateWebWindowRequest")
                    eggSafeDataStore.eggSafeView = eggSafeVi
                    eggSafeVi.setFileChooserHandler { callback ->
                        handleFileChooser(callback)
                    }
                    attachWebViewToContainer(eggSafeVi)
                }

                override fun eggSafeOnPermissionRequest(eggSafeRequest: PermissionRequest?) {
                    eggSafeRequest?.grant(eggSafeRequest.resources)
                }

                override fun eggSafeOnFirstPageFinished() {
                    eggSafeDataStore.eggSafeSetIsFirstFinishPage()
                }

            }, eggSafeWindow = requireActivity().window).apply {
                setFileChooserHandler { callback ->
                    handleFileChooser(callback)
                }
            }
            eggSafeDataStore.eggSafeView.eggSafeFLoad(arguments?.getString(EggSafeLoadFragment.EGGSAFE_SPLASH_DATA) ?: "")
//            ejvview.fLoad("www.google.com")
            eggSafeDataStore.eggSafeViList.add(eggSafeDataStore.eggSafeView)
            attachWebViewToContainer(eggSafeDataStore.eggSafeView)
        } else {
            eggSafeDataStore.eggSafeViList.forEach { webView ->
                webView.setFileChooserHandler { callback ->
                    handleFileChooser(callback)
                }
            }
            eggSafeDataStore.eggSafeView = eggSafeDataStore.eggSafeViList.last()

            attachWebViewToContainer(eggSafeDataStore.eggSafeView)
        }
        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "WebView list size = ${eggSafeDataStore.eggSafeViList.size}")
    }

    private fun handleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        eggSafeFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Launching file picker")
                    eggSafeTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Launching camera")
                    eggSafePhoto = eggSafeViFun.eggSafeSavePhoto()
                    eggSafeTakePhoto.launch(eggSafePhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                eggSafeFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun attachWebViewToContainer(w: EggSafeVi) {
        eggSafeDataStore.containerView.post {
            // Убираем предыдущую WebView, если есть
            (w.parent as? ViewGroup)?.removeView(w)
            eggSafeDataStore.containerView.removeAllViews()
            eggSafeDataStore.containerView.addView(w)
        }
    }



}