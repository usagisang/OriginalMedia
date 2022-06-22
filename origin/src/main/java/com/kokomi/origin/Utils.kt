package com.kokomi.origin

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal val main = Dispatchers.Main

internal val io = Dispatchers.IO

internal val default = Dispatchers.Default

internal suspend inline fun <T> io(noinline block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.IO, block)
}

internal suspend inline fun <T> main(noinline block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Main, block)
}

internal suspend inline fun <T> default(noinline block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Default, block)
}

private var toast: Toast? = null

internal fun Context.toast(msg: String) {
    toast?.cancel()
    toast = Toast.makeText(this, msg, Toast.LENGTH_LONG).apply { show() }
}

internal inline fun <reified V : View> View.view(@IdRes id: Int, block: V.() -> Unit = {}): V {
    return findViewById<V>(id).apply(block)
}

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

internal val String.html: Spanned
    get() = Html.fromHtml(this, FROM_HTML_MODE_LEGACY)