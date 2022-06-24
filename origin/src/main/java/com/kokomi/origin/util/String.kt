package com.kokomi.origin.util

import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned

internal val String.html: Spanned
    get() = Html.fromHtml(this, FROM_HTML_MODE_LEGACY)