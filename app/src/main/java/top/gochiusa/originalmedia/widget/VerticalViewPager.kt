package top.gochiusa.originalmedia.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs


class VerticalViewPager : ViewPager {
    var lastX = -1
    var lastY = -1

    constructor(@NonNull context: Context) : super(context) {}
    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(swapTouchEvent(MotionEvent.obtain(ev)))
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(swapTouchEvent(MotionEvent.obtain(ev)))
    }

    private fun swapTouchEvent(event: MotionEvent): MotionEvent {
        val width = width.toFloat()
        val height = height.toFloat()
        event.setLocation(event.y / height * width, event.x / width * height)
        return event
    }
/*
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.rawX.toInt()
        val y = ev.rawY.toInt()
        var dealtX = 0
        var dealtY = 0
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                dealtX = 0
                dealtY = 0
                // 保证子View能够接收到Action_move事件
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                dealtX += abs(x - lastX)
                dealtY += abs(y - lastY)
                // 这里是否拦截的判断依据是上下滑动，读者可根据自己的逻辑进行是否拦截
                Toast.makeText(context, "${parent.parent::class.java}", Toast.LENGTH_SHORT).show()
                if (dealtY >= dealtX) { // 上下滑动请求父 View 不要拦截
                    parent.requestDisallowInterceptTouchEvent(true)
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                lastX = x
                lastY = y
            }
            MotionEvent.ACTION_CANCEL -> {}
            MotionEvent.ACTION_UP -> {}
        }
        return super.dispatchTouchEvent(ev)
    }*/

}