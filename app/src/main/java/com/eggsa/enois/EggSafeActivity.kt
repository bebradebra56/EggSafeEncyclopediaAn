package com.eggsa.enois

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
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
        setContentView(R.layout.activity_egg_safe)
//        val rootView = findViewById<View>(android.R.id.content)
//        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Activity onCreate()")
        eggSafePushHandler.handlePush(intent.extras)
    }

}