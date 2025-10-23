package com.eggsa.enois

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Контроллер для управления системными барами (Navigation Bar и Status Bar)
 * Поддержка Android 7.0+ (API 24+)
 *
 * ВАЖНО: НЕ добавляйте android:configChanges в манифест!
 * Activity должна пересоздаваться для применения layout-land
 */
class SystemBarsController(private val activity: Activity) {

    private val window = activity.window
    private val decorView = window.decorView

    /**
     * Настраивает видимость системных баров в зависимости от ориентации
     * - Portrait: скрыт только Navigation Bar
     * - Landscape: скрыты Navigation Bar И Status Bar
     */
    fun setupSystemBars() {
        val isLandscape = activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            setupForApi30Plus(isLandscape)
        } else {
            // Android 7-10 (API 24-29)
            setupForApi24To29(isLandscape)
        }
    }

    /**
     * Настройка для Android 11+ (API 30+)
     * Использует WindowInsetsController
     */
    private fun setupForApi30Plus(isLandscape: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)

            val insetsController = window.insetsController ?: return

            // Настройка поведения при свайпе
            insetsController.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (isLandscape) {
                // Landscape: скрываем Navigation Bar и Status Bar
                insetsController.hide(
                    WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars()
                )
            } else {
                // Portrait: скрываем только Navigation Bar
                insetsController.hide(WindowInsets.Type.navigationBars())
                insetsController.show(WindowInsets.Type.statusBars())
            }
        }
    }

    /**
     * Настройка для Android 7-10 (API 24-29)
     * Использует системные флаги
     */
    @Suppress("DEPRECATION")
    private fun setupForApi24To29(isLandscape: Boolean) {
        // Базовые флаги для immersive режима
        var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        if (isLandscape) {
            // Landscape: добавляем флаги для скрытия Status Bar
            flags = flags or (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        } else {
            // Portrait: показываем Status Bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        decorView.systemUiVisibility = flags

        // Для Android 8+ (API 26+) делаем навигационный бар прозрачным
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            // Светлые иконки навигации (если нужно)
            decorView.systemUiVisibility = flags and
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }

    /**
     * Альтернативный метод с использованием WindowInsetsControllerCompat
     * Более универсальный подход через AndroidX
     */
    fun setupSystemBarsCompat() {
        val isLandscape = activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        if (isLandscape) {
            // Landscape: скрываем оба бара
            insetsController.hide(
                WindowInsetsCompat.Type.navigationBars() or
                        WindowInsetsCompat.Type.statusBars()
            )
        } else {
            // Portrait: только Navigation Bar
            insetsController.hide(WindowInsetsCompat.Type.navigationBars())
            insetsController.show(WindowInsetsCompat.Type.statusBars())
        }
    }
}

/**
 * Extension функция для Activity
 * Использование: activity.setupSystemBars()
 */
fun Activity.setupSystemBars() {
    SystemBarsController(this).setupSystemBars()
}

/**
 * Extension функция с поддержкой AndroidX
 * Использование: activity.setupSystemBarsCompat()
 */
fun Activity.setupSystemBarsCompat() {
    SystemBarsController(this).setupSystemBarsCompat()
}


// ============================================
// ПРАВИЛЬНОЕ ИСПОЛЬЗОВАНИЕ В ACTIVITY
// ============================================

/**
 * ✅ ПРАВИЛЬНЫЙ ВАРИАНТ
 * Activity пересоздается при смене ориентации → layout-land применяется автоматически
 */
/*
class EggSafeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ВАЖНО: вызвать ДО setContentView
        setupSystemBars()

        setContentView(R.layout.activity_eggsafe)
        // Android автоматически применит layout-land при повороте

        // Остальной код...
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        // Перенастраиваем при возврате фокуса
        if (hasFocus) {
            setupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()

        // Перенастраиваем при возврате в Activity
        setupSystemBars()
    }
}
*/

/**
 * ❌ НЕПРАВИЛЬНЫЙ ВАРИАНТ (не используйте!)
 * С android:configChanges="orientation" layout-land НЕ ПРИМЕНИТСЯ
 */
/*
class WrongActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSystemBars()
        setContentView(R.layout.activity_wrong)
    }

    // Этот метод вызовется вместо пересоздания Activity
    // Но layout-land НЕ применится автоматически!
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setupSystemBars() // системные бары обновятся
        // но layout останется портретным! ❌
    }
}
*/

/**
 * МАНИФЕСТ - БЕЗ android:configChanges
 */
/*
<activity
    android:name=".EggSafeActivity"
    android:theme="@style/Theme.YourApp.Fullscreen"
    android:screenOrientation="unspecified">
    <!-- НЕ добавляем android:configChanges="orientation" -->
</activity>
*/

/**
 * THEMES.XML
 */
/*
<style name="Theme.YourApp.Fullscreen" parent="Theme.MaterialComponents.DayNight.NoActionBar">
    <item name="android:windowFullscreen">false</item>
    <item name="android:windowDrawsSystemBarBackgrounds">true</item>
    <item name="android:statusBarColor">@android:color/transparent</item>
    <item name="android:navigationBarColor">@android:color/transparent</item>
</style>
*/

/**
 * СТРУКТУРА LAYOUT ФАЙЛОВ
 *
 * res/
 * ├── layout/
 * │   └── activity_eggsafe.xml          ← Portrait layout
 * └── layout-land/
 *     └── activity_eggsafe.xml          ← Landscape layout (применится автоматически)
 */