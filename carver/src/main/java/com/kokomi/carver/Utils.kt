package com.kokomi.carver.core

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Looper
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat

internal fun checkMainThread() {
    if (Looper.getMainLooper() != Looper.myLooper())
        throw IllegalStateException("This method must be called on the main thread")
}

// 获取状态栏高度
internal fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier(
        "status_bar_height",
        "dimen",
        "android"
    )
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

// 开启沉浸式状态栏和导航栏
internal fun Activity.clearSystemWindows(
    statusBar: TextView? = null,
    color: Int = Color.TRANSPARENT
) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    statusBar?.height = getStatusBarHeight()
    window.statusBarColor = color
    window.navigationBarColor = Color.TRANSPARENT
}

// 设置状态栏文本颜色
internal fun Activity.setStatusBarTextColor(isWhite: Boolean) {
    ViewCompat.getWindowInsetsController(window.decorView)
        ?.isAppearanceLightStatusBars = !isWhite
}