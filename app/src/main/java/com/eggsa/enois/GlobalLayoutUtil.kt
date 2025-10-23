package com.eggsa.enois

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout

//class GlobalLayoutUtil {
//
//    private var mChildOfContent: View? = null
//    private var frameLayoutParams: FrameLayout.LayoutParams? = null
//    private var usableHeightPrevious = 0
//
//    fun assistActivity(activity: Activity) {
//        val content = activity.findViewById<FrameLayout>(android.R.id.content)
//        mChildOfContent = content.getChildAt(0)
//        frameLayoutParams = mChildOfContent?.layoutParams as? FrameLayout.LayoutParams
//
//        mChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
//            possiblyResizeChildOfContent(activity)
//        }
//    }
//
//    private fun possiblyResizeChildOfContent(activity: Activity) {
//        val usableHeightNow = computeUsableHeight()
//        if (usableHeightNow != usableHeightPrevious) {
//            val usableHeightSansKeyboard = mChildOfContent?.rootView?.height ?: 0
//            val heightDifference = usableHeightSansKeyboard - usableHeightNow
//
//            if (heightDifference > (usableHeightSansKeyboard / 4)) {
//                // Клавиатура открыта — ресайзим height на screen - keyboard
//                frameLayoutParams?.height = usableHeightSansKeyboard - heightDifference
//            } else {
//                // Клавиатура закрыта — ресайзим обратно на full (учитывая nav bar)
//                frameLayoutParams?.height = usableHeightSansKeyboard - getNavigationBarHeight(activity)
//            }
//            mChildOfContent?.requestLayout()
//            usableHeightPrevious = usableHeightNow
//        }
//    }
//
//    private fun computeUsableHeight(): Int {
//        val r = Rect()
//        mChildOfContent?.getWindowVisibleDisplayFrame(r)
//        return r.bottom - r.top  // Visible height без status bar
//    }
//
//    private fun getNavigationBarHeight(activity: Activity): Int {
//        val resources = activity.resources
//        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
//        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
//    }
//}