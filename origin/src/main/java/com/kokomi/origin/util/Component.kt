package com.kokomi.origin.util

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.IdRes
import com.kokomi.origin.appContext

private var toast: Toast? = null

internal fun toast(msg: String) {
    toast?.cancel()
    toast = Toast.makeText(appContext, msg, Toast.LENGTH_LONG).apply { show() }
}

internal fun toastNetworkError() = toast("网络错误")

internal inline infix fun <reified V : View> Activity.find(@IdRes id: Int) = findViewById<V>(id)

internal inline fun <reified V : View> Activity.find(@IdRes id: Int, block: V.() -> Unit = {}): V {
    return findViewById<V>(id).apply(block)
}

internal fun Activity.keepScreenAlive(alive: Boolean = true) {
    if (alive) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}
