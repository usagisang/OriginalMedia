package top.gochiusa.originalmedia.widget;

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import top.gochiusa.originalmedia.R


/**
 * ClickableImageSpan 继承自 ImageSpan，使其能响应点击事件，并图片垂直居中显示
 * @author tangan
 */
abstract class ClickableImageSpan(b: Drawable?) : ImageSpan(b!!) {
    /** 图片垂直居中显示  */
    override fun getSize(
        paint: Paint, text: CharSequence?, start: Int, end: Int,
        fontMetricsInt: Paint.FontMetricsInt?
    ): Int {
        val drawable = drawable
        val rect: Rect = drawable.bounds
        if (fontMetricsInt != null) {
            val fmPaint: Paint.FontMetricsInt = paint.getFontMetricsInt()
            val fontHeight: Int = fmPaint.bottom - fmPaint.top
            val drHeight: Int = rect.bottom - rect.top
            val top = drHeight / 2 - fontHeight / 4
            val bottom = drHeight / 2 + fontHeight / 4
            fontMetricsInt.ascent = -bottom
            fontMetricsInt.top = -bottom
            fontMetricsInt.bottom = top
            fontMetricsInt.descent = top
        }
        return rect.right
    }

    /** 图片垂直居中显示  */
    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val drawable = drawable
        canvas.save()
        var transY = 0
        transY = (bottom - top - drawable.bounds.bottom) / 2 + top
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }

    /** 添加点击事件   */
    abstract fun onClick(view: View?)

    // 显示收缩状态的文本，设置点击图标，并添加点击事件
    open fun openFun(tv: TextView, ellipsizeStr: CharSequence, desc: String) {
        val temp: CharSequence = "$ellipsizeStr."
        val ssb = SpannableStringBuilder(temp)
        val dd = tv.resources.getDrawable(R.drawable.ic_baseline_expand_more_24)
        dd.setBounds(0, 0, dd.intrinsicWidth, dd.intrinsicHeight)
        val `is`: ClickableImageSpan = object : ClickableImageSpan(dd) {
            override fun onClick(view: View?) {
                closeFun(tv, ellipsizeStr, desc)
            }
        }
        ssb.setSpan(`is`, temp.length - 1, temp.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        tv.text = ssb
        tv.movementMethod = ClickableMovementMethod.instance
    }

    // 显示展开状态的文本，设置点击图标，并添加点击事件
    private fun closeFun(tv: TextView, ellipsizeStr: CharSequence, desc: String) {
        val ssb = SpannableStringBuilder(desc)
        val dd = tv.resources.getDrawable(R.drawable.ic_baseline_expand_less_24)
        dd.setBounds(0, 0, dd.intrinsicWidth, dd.intrinsicHeight)
        val `is`: ClickableImageSpan = object : ClickableImageSpan(dd) {
            override fun onClick(view: View?) {
                openFun(tv, ellipsizeStr, desc)
            }
        }
        ssb.setSpan(`is`, desc.length - 1, desc.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        tv.text = ssb
        tv.movementMethod = ClickableMovementMethod.instance
    }

}
