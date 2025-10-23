package com.eggsa.enois

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp
import com.eggsa.enois.eggsafeutils.presentation.pushhandler.EggSafePushHandler
import org.koin.android.ext.android.inject

class EggSafeActivity : AppCompatActivity() {

    lateinit var eggSafePhoto: Uri
    var eggSafeFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    val eggSafeTakeFile = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        eggSafeFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
    }

    val eggSafeTakePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            eggSafeFilePathFromChrome?.onReceiveValue(arrayOf(eggSafePhoto))
        } else {
            eggSafeFilePathFromChrome?.onReceiveValue(null)
        }
    }

    private val eggSafePushHandler by inject<EggSafePushHandler>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "onCreate : ${EggSafeApp.inputMode}")
        setupSystemBars()
//        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_egg_safe)
        val rootView = findViewById<View>(android.R.id.content)
//        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        GlobalLayoutUtil().assistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            // Устанавливаем padding для боковых сторон и верха
            val topPadding = maxOf(systemBars.top, displayCutout.top)
            val leftPadding = maxOf(systemBars.left, displayCutout.left)
            val rightPadding = maxOf(systemBars.right, displayCutout.right)
            window.setSoftInputMode(EggSafeApp.inputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "ADJUST PUN")
                val bottomInset = maxOf(systemBars.bottom, displayCutout.bottom)

                view.setPadding(leftPadding, topPadding, rightPadding, 0)

                // Изменяем layoutParams для учета нижнего отступа
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = bottomInset
                }
            } else {
                Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "ADJUST RESIZE")

                val bottomInset = maxOf(systemBars.bottom, displayCutout.bottom, ime.bottom)

                view.setPadding(leftPadding, topPadding, rightPadding, 0)

                // Изменяем layoutParams для учета нижнего отступа
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = bottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }

        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Activity onCreate()")
        eggSafePushHandler.handlePush(intent.extras)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        setupSystemBars()
    }

}