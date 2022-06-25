package com.kokomi.origin.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.kokomi.origin.appContext

internal inline infix fun <reified V : View> View.find(@IdRes id: Int) = findViewById<V>(id)

internal inline fun <reified V : View> View.find(@IdRes id: Int, block: V.() -> Unit = {}): V {
    return findViewById<V>(id).apply(block)
}

private var statusBarHeightInternal = -1

internal val Context.statusBarHeight: Int
    get() {
        if (statusBarHeightInternal != -1) return statusBarHeightInternal
        var result = 0
        val resourceId = resources.getIdentifier(
            "status_bar_height",
            "dimen",
            "android"
        )
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        statusBarHeightInternal = result
        return result
    }

internal val Fragment.statusBarHeight: Int
    get() = requireContext().statusBarHeight

private var navigationBarHeightInternal = -1

internal val Context.navigationBarHeight: Int
    get() {
        if (navigationBarHeightInternal != -1) return navigationBarHeightInternal
        var result = 0
        val resourceId = resources.getIdentifier(
            "navigation_bar_height",
            "dimen",
            "android"
        )
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
//        result = if (result < 50) 0 else result
        navigationBarHeightInternal = result
        return result
    }

internal val Fragment.navigationBarHeight: Int
    get() = requireContext().navigationBarHeight

internal fun Activity.clearSystemBar(
    statusBar: TextView? = null,
    navigationBar: TextView? = null,
    statusBarColor: Int = Color.TRANSPARENT,
    navigationBarColor: Int = Color.TRANSPARENT
) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    statusBar?.height = statusBarHeight
    navigationBar?.height = navigationBarHeight
    window.statusBarColor = statusBarColor
    window.navigationBarColor = navigationBarColor
}

internal fun Activity.statusBarTextColor(isWhite: Boolean) {
    ViewCompat.getWindowInsetsController(window.decorView)
        ?.isAppearanceLightStatusBars = !isWhite
}

internal val Int.pxToDp: Dp
    get() = (this / appContext.resources.displayMetrics.density + 0.5f).dp