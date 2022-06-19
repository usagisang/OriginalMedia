package top.gochiusa.originalmedia.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager
import kotlin.math.abs


/**
 *
 */
class HorViewPager : ViewPager {
    private var mDownPosX = 0f
    private var mDownPosY = 0f
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.x
        val y = ev.y
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownPosX = x
                mDownPosY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX: Float = abs(x - mDownPosX)
                val deltaY: Float = abs(y - mDownPosY)
                // 这里是否拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
                if (deltaX > deltaY) { // 左右滑动不拦截
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}