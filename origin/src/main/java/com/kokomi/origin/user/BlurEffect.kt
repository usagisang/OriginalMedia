// Android 12 以下要用以前的方法来实现高斯模糊
@file:Suppress("DEPRECATION")

package com.kokomi.origin.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes

internal fun Context.getBlurBitmap(bitmap: Bitmap, radius: Float): Bitmap {
    val renderScript = RenderScript.create(this)
    val scriptIntrinsicBlur =
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
    val result = Bitmap.createBitmap(bitmap)
    val input = Allocation.createFromBitmap(renderScript, bitmap)
    val output = Allocation.createTyped(renderScript, input.type)
    scriptIntrinsicBlur.setRadius(radius)
    scriptIntrinsicBlur.setInput(input)
    scriptIntrinsicBlur.forEach(output)
    output.copyTo(result)
    return result
}

internal fun Context.getBitmap(@DrawableRes drawableId: Int): Bitmap {
    val bitmap: Bitmap
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        val vectorDrawable = getDrawable(drawableId)!!
        bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
    } else {
        bitmap = BitmapFactory.decodeResource(resources, drawableId)
    }
    return bitmap
}