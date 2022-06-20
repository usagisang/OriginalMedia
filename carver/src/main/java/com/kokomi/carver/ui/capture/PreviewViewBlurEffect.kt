// Android 12 以下要用以前的方法来实现高斯模糊
@file:Suppress("DEPRECATION")

package com.kokomi.carver.ui.capture

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import kotlinx.coroutines.delay

/**
 * [PreviewViewBlurEffect] 用于实现切换镜头时，界面的高斯模糊效果
 * */
class PreviewViewBlurEffect(
    context: Context,
    private var bitmap: Bitmap,
    private val imageView: ImageView
) {

    private val renderScript = RenderScript.create(context)

    private val scriptIntrinsicBlur =
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

    @Suppress("UNUSED")
    private fun setBlur(radius: Float) {
        val result = Bitmap.createBitmap(bitmap)
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createTyped(renderScript, input.type)
        scriptIntrinsicBlur.setRadius(radius)
        scriptIntrinsicBlur.setInput(input)
        scriptIntrinsicBlur.forEach(output)
        output.copyTo(result)
        bitmap = result
        imageView.setImageBitmap(result)
    }

    /**
     * 调用此函数，开启高斯模糊动画
     * */
    suspend fun startAnim(start: Float = 0.1f, end: Float = 25f, d: Long = 300L) {
        imageView.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(
            this,
            "blur",
            start,
            end
        ).apply {
            duration = d
            interpolator = DecelerateInterpolator()
        }.start()
        delay(2 * d)
        imageView.visibility = View.GONE
    }

}