package com.kokomi.origin.util

import android.view.View
import androidx.annotation.IdRes

internal inline fun <reified V : View> View.view(@IdRes id: Int, block: V.() -> Unit = {}): V {
    return findViewById<V>(id).apply(block)
}