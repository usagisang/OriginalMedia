package com.kokomi.origin.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.kokomi.origin.appContext

private var toast: Toast? = null

internal fun toast(msg: String) {
    toast?.cancel()
    toast = Toast.makeText(appContext, msg, Toast.LENGTH_LONG).apply { show() }
}

internal fun toastNetworkError() = toast("网络错误")

internal inline fun <reified V : View> Activity.view(@IdRes id: Int, block: V.() -> Unit = {}): V {
    return findViewById<V>(id).apply(block)
}

private var statusBarHeight = -1

internal fun Context.getStatusBarHeight(): Int {
    if (statusBarHeight != -1) return statusBarHeight
    var result = 0
    val resourceId = resources.getIdentifier(
        "status_bar_height",
        "dimen",
        "android"
    )
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    statusBarHeight = result
    return result
}

internal fun Fragment.getStatusBarHeight() = requireContext().getStatusBarHeight()

internal fun Activity.clearSystemWindows(
    statusBar: TextView? = null,
    color: Int = Color.TRANSPARENT
) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    statusBar?.height = getStatusBarHeight()
    window.statusBarColor = color
    window.navigationBarColor = Color.TRANSPARENT
}

internal fun Activity.statusBarTextColor(isWhite: Boolean) {
    ViewCompat.getWindowInsetsController(window.decorView)
        ?.isAppearanceLightStatusBars = !isWhite
}

internal fun Activity.keepScreenAlive(alive: Boolean = true) {
    if (alive) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}