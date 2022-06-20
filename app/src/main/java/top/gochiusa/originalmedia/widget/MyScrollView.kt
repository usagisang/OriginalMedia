package top.gochiusa.originalmedia.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView


class MyScrollView : ScrollView {

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        maxY = getChildAt(0).measuredHeight - measuredHeight
    }

    //在Y轴上可以滑动的最大距离 = 总长度 - 当前展示区域长度
    private var maxY = 0
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> parent.parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE -> if (scrollY in 1 until maxY) parent.requestDisallowInterceptTouchEvent(
                true
            ) else parent.requestDisallowInterceptTouchEvent(false)
            MotionEvent.ACTION_UP -> parent.parent.requestDisallowInterceptTouchEvent(false)
        }
        return super.dispatchTouchEvent(ev)
    }
}