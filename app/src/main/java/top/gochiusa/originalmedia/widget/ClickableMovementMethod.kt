package top.gochiusa.originalmedia.widget;

import android.text.Layout
import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView


/**
 * ClickableMovementMethod 继承自 LinkMovementMethod，使其能响应 ClickableImageSpan
 * @author tangan
 */
class ClickableMovementMethod : LinkMovementMethod() {
    override fun onTouchEvent(
        widget: TextView, buffer: Spannable,
        event: MotionEvent
    ): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_DOWN
        ) {
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY
            val layout: Layout = widget.layout
            val line: Int = layout.getLineForVertical(y)
            val off: Int = layout.getOffsetForHorizontal(line, x.toFloat())
            val link = buffer.getSpans(
                off, off,
                ClickableSpan::class.java
            )

            /** 修改位置【1】 START  */
            val imageSpans = buffer.getSpans(
                off, off,
                ClickableImageSpan::class.java
            )
            /******    END     */
            if (link.isNotEmpty()) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget)
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(
                        buffer,
                        buffer.getSpanStart(link[0]),
                        buffer.getSpanEnd(link[0])
                    )
                }
                return true
            } else if (imageSpans.isNotEmpty()) {
                if (action == MotionEvent.ACTION_UP) {
                    imageSpans[0].onClick(widget)
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(
                        buffer,
                        buffer.getSpanStart(imageSpans[0]),
                        buffer.getSpanEnd(imageSpans[0])
                    )
                }
                return true
            } else {
                Selection.removeSelection(buffer)
            }
        }
        return false
    }

    companion object {
        private var sInstance: ClickableMovementMethod? = null
        val instance: ClickableMovementMethod?
            get() {
                if (sInstance == null) {
                    sInstance = ClickableMovementMethod()
                }
                return sInstance
            }
    }
}