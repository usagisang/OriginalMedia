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

    // 绘制单击聚焦时的矩形（聚焦框）
    override fun onDraw(canvas: Canvas) {
        if (clickX > 0f && clickY > 0f) {
            // 画矩形
            canvas.drawRoundRect(
                clickX - rectLength,
                clickY - rectLength,
                clickX + rectLength,
                clickY + rectLength,
                rectLength / 10, rectLength / 10,
                rectPaint
            )

            // 画中间的两条线
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

    // 聚焦框的缩小动画
    private var zoomEffectAnim: Animator? = null

    // 聚焦框的闪烁动画
    private var twinkleEffectAnim: Animator? = null

    // 聚焦框的不透明变半透明动画
    private var translucentEffectAnim: Animator? = null

    // 聚焦框的半透明变全透明（消失）动画
    private var transparentEffectAnim: Animator? = null

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                // 双指触摸时记录开始的双指距离
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
                // 双指移动时计算双指距离，得到一个跟随手指的放大倍数
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
                // 抬起手指时判断是否为点击事件，点击事件则绘制聚焦框
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

    // Start --->>> 下面是实现动画效果的函数
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

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

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // End --->>> 上面是实现动画效果的函数

    // Start --->>> 下面是监听的接口
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private var zoomGestureListener: ((Float) -> Unit)? = null

    /**
     * 设置缩放倍数（双指手势）的监听
     * */
    fun setZoomChangedListener(listener: (Float) -> Unit) {
        zoomGestureListener = listener
    }

    private var clickListener: ((Float, Float) -> Unit)? = null

    /**
     * 设置点击事件的监听，函数中回传的参数为点击位置的 x 坐标和 y 坐标
     * */
    fun setClickListener(listener: (Float, Float) -> Unit) {
        clickListener = listener
    }

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // End --->>> 上面是监听的接口

}