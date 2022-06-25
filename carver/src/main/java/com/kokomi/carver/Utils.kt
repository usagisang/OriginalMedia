package com.kokomi.carver

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private var toast: Toast? = null

/**
 * Toast 工具，防止重复弹 Toast
 * */
internal fun Context.toast(msg: String) {
    toast?.cancel()
    toast = Toast.makeText(this, msg, Toast.LENGTH_LONG).apply { show() }
}

/**
 * 检查线程
 * */
internal fun checkMainThread() {
    if (Looper.getMainLooper() != Looper.myLooper())
        throw IllegalStateException("This method must be called on the main thread.")
}

/**
 * 获取状态栏高度
 * */
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

/**
 * 开启沉浸式状态栏和导航栏
 * */
internal fun Activity.clearSystemWindows(
    statusBar: TextView? = null,
    color: Int = Color.TRANSPARENT
) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    statusBar?.height = getStatusBarHeight()
    window.statusBarColor = color
    window.navigationBarColor = Color.TRANSPARENT
}

/**
 * 设置状态栏文本颜色
 * */
internal fun Activity.setStatusBarTextColor(isWhite: Boolean) {
    ViewCompat.getWindowInsetsController(window.decorView)
        ?.isAppearanceLightStatusBars = !isWhite
}

/**
 * 默认的视频文件存储路径
 * */
fun Activity.defaultOutputDirectory(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, packageName).apply { mkdir() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else filesDir
}

/**
 * 格式化录制时长
 * */
internal fun formatRecordingTime(nanos: Long): String {
    val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    return formatter.format(Date(nanos / 1000_000))
}
