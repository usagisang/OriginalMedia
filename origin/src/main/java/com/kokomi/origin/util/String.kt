package com.kokomi.origin.util

import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned
import java.text.DecimalFormat

private val dateArray = arrayOf("年", "月", "日")

internal val String.html: Spanned
    get() = Html.fromHtml(this, FROM_HTML_MODE_LEGACY)

/**
 * 将格式为2022-02-02 12:12:12的字符串解析为"suffix 2022年02月02日"
 */
internal fun getFormatDate(suffix: String, date: String): String {
    return date.substring(0, 10).run {
        var i = 0
        val builder = StringBuilder()

        builder.append(suffix)
        forEach {
            if (it == '-') {
                builder.append(dateArray[i++])
            } else {
                builder.append(it)
            }
        }
        builder.append(dateArray[i])
        builder.toString()
    }
}

/**
 * 将以毫秒为单位的十进制时间转换为00:00的60进制形式
 */
internal fun toTimeText(progress: Float): String = toTimeText(progress.toLong())

internal fun toTimeText(milliSecond: Long): String {
    val totalSecond = (milliSecond / 1000).toInt()
    val totalMinute = totalSecond / 60
    val second = totalSecond % 60
    val builder = StringBuilder()
    val decimalFormat = DecimalFormat("00")
    builder.append(decimalFormat.format(totalMinute))
    builder.append(':')
    builder.append(decimalFormat.format(second))
    return builder.toString()
}