package com.kokomi.origin.weight

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class OriginScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    val canScroll: Boolean
        get() {
            val childHeight = getChildAt(0)?.height ?: return false
            return height < childHeight
        }

    var isScrollable = true

    private var downY = -1f

    private var topSecond = false
    private var bottomSecond = false

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // 不可滑动状态，不处理任何触摸事件
        if (!isScrollable) return false
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downY = ev.y
                if (isTop) topSecond = true
                if (isBottom) bottomSecond = true
            }
            MotionEvent.ACTION_MOVE -> {
                val lastY = downY
                downY = ev.y
                if (topSecond && (isTop && downY - lastY > 0)) {
                    parent.requestDisallowInterceptTouchEvent(false)
                    topSecond = false
                    return false
                } else if (bottomSecond && (isBottom && downY - lastY < 0)) {
                    parent.requestDisallowInterceptTouchEvent(false)
                    bottomSecond = false
                    return false
                }
                topSecond = false
                bottomSecond = false
            }
        }
        // 其余情况交由 ScrollView 处理
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(isScrollable)
        return super.onInterceptTouchEvent(ev)
    }

    private val isTop: Boolean
        get() = scrollY == 0

    private val isBottom: Boolean
        get() {
            val childHeight = getChildAt(0)?.height ?: return true
            // 如果当前不可滚动，则必定返回 true
            return height + scrollY >= childHeight
        }

}