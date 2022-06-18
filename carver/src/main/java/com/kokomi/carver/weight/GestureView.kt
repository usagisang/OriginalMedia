package com.kokomi.carver.weight

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 用于监听双指放大手势和点击聚焦手势的 View
 * */
class GestureView : View {

    constructor (context: Context)
            : this(context, null)

    constructor (context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0)

    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    override fun isClickable() = true

    override fun isFocused() = true

    private var zoom: Float = 0f

    private var distance = 0f

    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.rgb(0xEE, 0xEE, 0xEE)
    }

    private var clickX = -1f

    private var clickY = -1f

    private var isZoomGesture = false

    private var rectLength = 160f

    override fun onDraw(canvas: Canvas) {
        if (clickX > 0f && clickY > 0f) {
            canvas.drawRoundRect(
                clickX - rectLength,
                clickY - rectLength,
                clickX + rectLength,
                clickY + rectLength,
                rectLength / 10, rectLength / 10,
                rectPaint
            )
            val lineLength = rectLength / 5
            canvas.drawLine(
                clickX - lineLength,
                clickY,
                clickX + lineLength,
                clickY,
                rectPaint
            )
            canvas.drawLine(
                clickX,
                clickY - lineLength,
                clickX,
                clickY + lineLength,
                rectPaint
            )
        }
    }

    private var zoomEffectAnim: Animator? = null
    private var twinkleEffectAnim: Animator? = null
    private var translucentEffectAnim: Animator? = null
    private var transparentEffectAnim: Animator? = null

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    val p0x = event.getX(0)
                    val p0y = event.getY(0)
                    val p1x = event.getX(1)
                    val p1y = event.getY(1)
                    distance = sqrt((p0x - p1x).pow(2) + (p0y - p1y).pow(2))
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    val p0x = event.getX(0)
                    val p0y = event.getY(0)
                    val p1x = event.getX(1)
                    val p1y = event.getY(1)
                    val d = sqrt((p0x - p1x).pow(2) + (p0y - p1y).pow(2))
                    val z = zoom + (d - distance) / (2 * width)
                    distance = d
                    zoom = if (z <= 0f) 0f else if (z >= 1f) 1f else z
                    zoomGestureListener?.invoke(zoom)
                    isZoomGesture = true
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isZoomGesture) {
                    isZoomGesture = false
                    return true
                }
                clickX = event.x
                clickY = event.y
                startRectEffectAnim()
                clickListener?.invoke(clickX, clickY)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    @Suppress("UNUSED")
    private fun setZoomEffect(length: Float) {
        rectLength = length
        rectPaint.alpha = (255 * ((length - 100f) / 100f)).toInt()
        invalidate()
    }

    @Suppress("UNUSED")
    private fun setRectTwinkleEffect(alpha: Int) {
        rectPaint.alpha = alpha
        invalidate()
    }

    @Suppress("UNUSED")
    private fun setRectTranslucent(degree: Int) {
        rectPaint.alpha = degree
        invalidate()
    }

    @Suppress("UNUSED")
    private fun setRectTransparent(degree: Int) {
        rectPaint.alpha = degree
        invalidate()
    }

    private fun startRectEffectAnim() {
        zoomEffectAnim = ObjectAnimator.ofFloat(
            this,
            "ZoomEffect",
            150f,
            100f
        ).apply {
            setAutoCancel(true)
            duration = 300L
            interpolator = DecelerateInterpolator()
            start()
        }
        twinkleEffectAnim = ObjectAnimator.ofInt(
            this,
            "RectTwinkleEffect",
            0, 255, 0, 255, 0, 200
        ).apply {
            setAutoCancel(true)
            duration = 2000L
            startDelay = 300L
            interpolator = null
            start()
        }
        translucentEffectAnim = ObjectAnimator.ofInt(
            this,
            "RectTransparent",
            200, 80
        ).apply {
            setAutoCancel(true)
            duration = 500L
            startDelay = 5000L
            interpolator = null
            start()
        }
        transparentEffectAnim = ObjectAnimator.ofInt(
            this,
            "RectTranslucent",
            80, 0
        ).apply {
            setAutoCancel(true)
            duration = 500L
            startDelay = 7000L
            interpolator = null
            start()
        }
    }

    fun clearZoom() {
        zoom = 0f
    }

    fun clearRect() {
        twinkleEffectAnim?.cancel()
        translucentEffectAnim?.cancel()
        transparentEffectAnim?.cancel()
        rectPaint.alpha = 0
        invalidate()
    }

    private var zoomGestureListener: ((Float) -> Unit)? = null

    fun setZoomChangedListener(listener: (Float) -> Unit) {
        zoomGestureListener = listener
    }

    private var clickListener: ((Float, Float) -> Unit)? = null

    fun setClickListener(listener: (Float, Float) -> Unit) {
        clickListener = listener
    }

}