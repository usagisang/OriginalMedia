package com.kokomi.origin.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.FileUtils
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.IdRes
import com.kokomi.carver.defaultOutputDirectory
import com.kokomi.origin.appContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.random.Random

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

// 通过 uri 得到输入流信息，包括输入流和文件拓展名
internal infix fun Context.getInputStreamInfoFrom(uri: Uri) = Pair(
    contentResolver.openInputStream(uri)!!,
    MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri)) ?: ""
)