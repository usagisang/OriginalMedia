package com.kokomi.carver.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CircleProgressBar : View {

    constructor (context: Context)
            : this(context, null)

    constructor (context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0)

    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 16f
        strokeCap = Paint.Cap.ROUND
        color = Color.rgb(0xF5, 0x00, 0x00)
    }

    override fun onDraw(canvas: Canvas) {
        val length = min(width, height)
        val angle = progress / maxProgress.toFloat()
        canvas.drawArc(
            8f, 8f, length - 8f, length - 8f,
            -90f, 360 * angle, false, circlePaint
        )
    }

    private var maxProgress = 10L
    private var progress = 6L

    fun setProgress(current: Long) {
        progress = current
        invalidate()
    }

    fun setMaxProgress(max: Long) {
        maxProgress = max
    }

}