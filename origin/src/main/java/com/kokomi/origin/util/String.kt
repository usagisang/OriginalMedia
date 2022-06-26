package com.kokomi.origin.util

import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned

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