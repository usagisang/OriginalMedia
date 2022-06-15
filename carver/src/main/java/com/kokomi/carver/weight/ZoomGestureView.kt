package com.kokomi.carver.weight

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 用于监听双指放大手势的 View
 * */
class ZoomGestureView : View {

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
                    println("d = $d")
                    val z = zoom + (d - distance) / (2 * width)
                    distance = d
                    zoom = if (z <= 0f) 0f else if (z >= 1f) 1f else z
                    mListener?.invoke(zoom)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private var mListener: ((Float) -> Unit)? = null

    fun setZoomChangedListener(listener: (Float) -> Unit) {
        mListener = listener
    }

}