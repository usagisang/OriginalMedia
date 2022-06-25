package com.kokomi.origin.util

import android.app.Activity
import android.content.ContentResolver
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

internal fun Activity.copyAndLoadBitmap(uri: Uri): File {
    var file: File? = null
    if (uri.scheme.equals(ContentResolver.SCHEME_FILE)) {
        return File(uri.path!!)
    }
    if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        val displayName = "${System.currentTimeMillis() + Random.nextLong()}" +
                ".${
                    MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(
                            contentResolver.getType(uri)
                        )
                }"
        try {
            val input = contentResolver.openInputStream(uri)!!
            val cache = File(defaultOutputDirectory(), displayName)
            val fos = FileOutputStream(cache)
            FileUtils.copy(input, fos)
            file = cache
            fos.close()
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return file!!
}