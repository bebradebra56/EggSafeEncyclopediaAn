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
import com.eggsa.enois.eggsafeutils.presentation.ui.load.EggSafeLoadFragment
import org.koin.android.ext.android.inject

class EggSafeV : Fragment(){

    private val dataStore by activityViewModels<EggSafeDataStore>()
    private lateinit var ejvview: EggSafeVi
    lateinit var eggSafeRequestFromChrome: PermissionRequest

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Разрешение выдано — запускаем выбор картинки
                (requireActivity() as EggSafeActivity).eggSafeTakeFile.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                onPermissionDenied((requireActivity() as EggSafeActivity).eggSafeFilePathFromChrome)
            }
        }


    private val eggSafeViFun by inject<EggSafeViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (ejvview.canGoBack()) {
                        ejvview.goBack()
                    } else if (dataStore.eggSafeViList.size > 1) {
                        this.isEnabled = false
                        dataStore.eggSafeViList.removeAt(dataStore.eggSafeViList.lastIndex)
                        ejvview.destroy()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        viewLifecycleOwner.lifecycleScope.launch {
//            dataStore.isFirstFinishPage.collect {
//                if (it) {
//                    frameLayout.apply {
//                        layoutParams = ViewGroup.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.MATCH_PARENT
//                        )
////                        addView(ImageView(requireContext()).apply {
////                            layoutParams = ViewGroup.LayoutParams(
////                                ViewGroup.LayoutParams.MATCH_PARENT,
////                                ViewGroup.LayoutParams.MATCH_PARENT
////                            )
////                            setImageResource(R.drawable.bg)
////                            scaleType = ImageView.ScaleType.CENTER_CROP
////                        })
//                        addView(ProgressBar(requireContext()).apply {
//                            val sizeInPx = (100 * resources.displayMetrics.density).toInt()
//                            layoutParams = FrameLayout.LayoutParams(sizeInPx, sizeInPx).apply {
//                                gravity = Gravity.CENTER
//                            }
//                            indeterminateTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
//                        })
//                        visibility = View.VISIBLE
//                        (view as? ViewGroup)?.addView(frameLayout)
//                    }
//                } else {
//                    frameLayout.visibility = View.GONE
//                    (view as? ViewGroup)?.removeView(frameLayout)
//                }
//            }
//        }


        if (dataStore.eggSafeViList.isEmpty()) {
            ejvview = EggSafeVi(requireContext(), object : EggSafeCallBack {
                override fun handleCreateWebWindowRequest(EggSafeVi: EggSafeVi) {
                    dataStore.eggSafeViList.add(EggSafeVi)
                    findNavController().navigate(R.id.action_eggSafeV_self)
                }

                override fun onPermissionRequest(request: PermissionRequest?) {
                    if (request != null) {
                        eggSafeRequestFromChrome = request
                    }
                    eggSafeRequestFromChrome.grant(eggSafeRequestFromChrome.resources)
                }

                override fun onShowFileChooser(filePathCallback: ValueCallback<Array<Uri>>?) {
                    (requireActivity() as EggSafeActivity).eggSafeFilePathFromChrome = filePathCallback
                    val listItems: Array<out String> =
                        arrayOf("Select from file", "To make a photo")
                    val listener = DialogInterface.OnClickListener { _, which ->
                        when (which) {
                            0 -> {
                                (requireActivity() as EggSafeActivity).eggSafeTakeFile.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
//                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
//                                    val permission = Manifest.permission.READ_MEDIA_IMAGES
//                                    if (ContextCompat.checkSelfPermission(requireContext(), permission)
//                                        == PackageManager.PERMISSION_GRANTED
//                                    ) {
//                                        // Уже разрешено
//                                        (requireActivity() as EggSafeActivity).eggSafeTakeFile.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//                                    } else {
//                                        // Запрашиваем
//                                        requestPermissionLauncher.launch(permission)
//                                    }
//                                } else {
//                                    val permission = Manifest.permission.READ_EXTERNAL_STORAGE
//                                    if (ContextCompat.checkSelfPermission(requireContext(), permission)
//                                        == PackageManager.PERMISSION_GRANTED
//                                    ) {
//                                        // Уже разрешено
//                                        (requireActivity() as EggSafeActivity).eggSafeTakeFile.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//                                    } else {
//                                        // Запрашиваем
//                                        requestPermissionLauncher.launch(permission)
//                                    }
//                                }
                            }
                            1 -> {
//                                val permission = Manifest.permission.CAMERA
//                                if (ContextCompat.checkSelfPermission(requireContext(), permission)
//                                    == PackageManager.PERMISSION_GRANTED
//                                ) {
//                                    // Уже разрешено
//                                    (requireActivity() as EggSafeActivity).eggSafePhoto = eggSafeViFun.eggSafeSavePhoto()
//                                    (requireActivity() as EggSafeActivity).eggSafeTakePhoto.launch((requireActivity() as EggSafeActivity).eggSafePhoto)
//                                } else {
//                                    // Запрашиваем
//                                    requestPermissionLauncher.launch(permission)
//                                }
                                (requireActivity() as EggSafeActivity).eggSafePhoto = eggSafeViFun.eggSafeSavePhoto()
                                (requireActivity() as EggSafeActivity).eggSafeTakePhoto.launch((requireActivity() as EggSafeActivity).eggSafePhoto)
                            }
                        }
                    }
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Choose a method")
                        .setItems(listItems, listener)
                        .setCancelable(true)
                        .setOnCancelListener {
                            filePathCallback?.onReceiveValue(arrayOf(Uri.EMPTY))
                        }
                        .create()
                        .show()
                }

                override fun onFirstPageFinished() {
                    dataStore.setIsFirstFinishPage()
                }

            }, window = requireActivity().window)
            ejvview.fLoad(arguments?.getString(EggSafeLoadFragment.EGGSAFE_SPLASH_DATA) ?: "")
//            ejvview.fLoad("www.google.com")
            dataStore.eggSafeViList.add(ejvview)
        } else {
            ejvview = dataStore.eggSafeViList.last()
        }
//        val rootView = requireActivity().findViewById<View>(android.R.id.content)
//        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            val displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
//            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
//
//            val topPadding = maxOf(systemBars.top, displayCutout.top)
//            val leftPadding = maxOf(systemBars.left, displayCutout.left)
//            val rightPadding = maxOf(systemBars.right, displayCutout.right)
//            val bottomPadding = maxOf(systemBars.bottom, displayCutout.bottom)  // Только навбар, игнорим IME для no resize
//
//            view.setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
//
//            // Detect keyboard open (ime.bottom > 0) и scroll WebView к input via JS
//            if (ime.bottom > 0) {  // Клавиатура visible
//                // Получи WebView из dataStore (предполагаю, что ejvview доступен; если нет, сделай broadcast или callback)
//                dataStore.eggSafeViList.lastOrNull()?.let { webView ->
//                    webView.evaluateJavascript(
//                        "if (document.activeElement) { document.activeElement.scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'}); }",
//                        null
//                    )
//                }
//            }
//
//            insets  // Возвращаем insets без consume, чтобы другие views видели IME если нужно
//        }
//        val screenHeight = resources.displayMetrics.heightPixels
//        val screenWidth = resources.displayMetrics.widthPixels
//
//        val scrollView = ScrollView(requireContext()).apply {
//            layoutParams = FrameLayout.LayoutParams(
//                screenWidth,
//                screenHeight
//            )
//            isFillViewport = true  // Растягивает child (WebView) на всю высоту ScrollView
//
//            if (ejvview.parent != null) {
//                (ejvview.parent as ViewGroup).removeView(ejvview)
//            }
//            addView(ejvview)  // Добавляем WebView внутрь
//        }
//        return scrollView
        return ejvview
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        ejvview.requestFocus()
//
//        val rootView = requireActivity().findViewById<View>(android.R.id.content)
//
//        rootView.viewTreeObserver.addOnGlobalLayoutListener {
//            val rect = Rect()
//            rootView.getWindowVisibleDisplayFrame(rect)
//
//            val screenHeight = rootView.rootView.height
//            val keyboardHeight = screenHeight - rect.bottom
//            val isKeyboardVisible = keyboardHeight > screenHeight * 0.15
//
//            if (isKeyboardVisible) {
//                // 🔹 JS: проверяем активный элемент
//                ejvview.evaluateJavascript(
//                    """
//                (function() {
//                    const el = document.activeElement;
//                    if (!el) return false;
//                    const rect = el.getBoundingClientRect();
//                    return { bottom: rect.bottom, viewportHeight: window.innerHeight };
//                })();
//                """.trimIndent()
//                ) { json ->
//                    try {
//                        if (json != "null") {
//                            val obj = org.json.JSONObject(json)
//                            val elementBottom = obj.getDouble("bottom")
//                            val viewportHeight = obj.getDouble("viewportHeight")
//
//                            // Если элемент перекрыт клавиатурой → имитируем adjustResize
//                            val isCovered = elementBottom > viewportHeight - keyboardHeight
//
//                            if (isCovered) {
//                                // Scroll или resize WebView по необходимости
//                                ejvview.updateLayoutParams<FrameLayout.LayoutParams> {
//                                    height = rect.bottom // имитируем adjustResize
//                                }
//
//                                // Дополнительно, прокручиваем к элементу
//                                ejvview.evaluateJavascript(
//                                    "document.activeElement.scrollIntoView({behavior:'smooth', block:'center'});",
//                                    null
//                                )
//                            } else {
//                                // Не перекрыт → оставляем WebView без изменений
//                                ejvview.updateLayoutParams<FrameLayout.LayoutParams> {
//                                    height = FrameLayout.LayoutParams.MATCH_PARENT
//                                }
//                            }
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            } else {
//                // Клавиатура закрыта → WebView на весь экран
//                ejvview.updateLayoutParams<FrameLayout.LayoutParams> {
//                    height = FrameLayout.LayoutParams.MATCH_PARENT
//                }
//            }
//        }
//    }





    private fun onPermissionDenied(filePathCallback: ValueCallback<Array<Uri>>?) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("Media permission is permanently denied. Please allow it in settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                filePathCallback?.onReceiveValue(null)
                (requireActivity() as EggSafeActivity).eggSafeFilePathFromChrome = filePathCallback
                startActivity(intent)
            }
            .setOnCancelListener {
                filePathCallback?.onReceiveValue(null)
                (requireActivity() as EggSafeActivity).eggSafeFilePathFromChrome = filePathCallback
            }
            .setNegativeButton("Cancel") { _, _ ->
                filePathCallback?.onReceiveValue(null)
                (requireActivity() as EggSafeActivity).eggSafeFilePathFromChrome = filePathCallback
            }
            .show()
    }



}