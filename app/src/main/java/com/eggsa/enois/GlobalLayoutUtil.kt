package com.eggsa.enois

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp

class GlobalLayoutUtil {

    private var eggSafeMChildOfContent: View? = null
    private var eggSafeUsableHeightPrevious = 0

    fun eggSafeAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        eggSafeMChildOfContent = content.getChildAt(0)

        eggSafeMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val eggSafeUsableHeightNow = eggSafeComputeUsableHeight()
        if (eggSafeUsableHeightNow != eggSafeUsableHeightPrevious) {
            val eggSafeUsableHeightSansKeyboard = eggSafeMChildOfContent?.rootView?.height ?: 0
            val eggSafeHeightDifference = eggSafeUsableHeightSansKeyboard - eggSafeUsableHeightNow

            if (eggSafeHeightDifference > (eggSafeUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(EggSafeApp.inputMode)
            } else {
                activity.window.setSoftInputMode(EggSafeApp.inputMode)
            }
//            mChildOfContent?.requestLayout()
            eggSafeUsableHeightPrevious = eggSafeUsableHeightNow
        }
    }

    private fun eggSafeComputeUsableHeight(): Int {
        val r = Rect()
        eggSafeMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}