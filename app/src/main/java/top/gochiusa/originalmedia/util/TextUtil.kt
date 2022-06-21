package top.gochiusa.originalmedia.util

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.widget.ClickableImageSpan
import top.gochiusa.originalmedia.widget.ClickableMovementMethod

object TextUtil {
    /**
     * 添加监听
     * @param tv   要实现伸缩效果的 TextView
     * @param desc TextView 要展示的文字
     */
    fun toggleEllipsize(tv: TextView, desc: String?) {
        if (desc == null) {
            return
        }

        //去除点击图片后的背景色（ SpannableString 在点击时会使背景变色 ，填上这句则可不变色 ）
        tv.highlightColor = Color.TRANSPARENT

        //添加 TextView 的高度监听
        tv.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("NewApi")
            override fun onGlobalLayout() {
                val paddingLeft = tv.paddingLeft
                val paddingRight = tv.paddingRight
                val paint = tv.paint
                val moreText = tv.textSize * 3
                val availableTextWidth = (tv.width - paddingLeft - paddingRight) * 2 - moreText
                val ellipsizeStr =
                    TextUtils.ellipsize(desc, paint, availableTextWidth, TextUtils.TruncateAt.END)

                // TextView 实际显示的文本长度  < 应该显示文本的长度(收缩状态)
                if (ellipsizeStr.length < desc.length) {
                    openFun(tv, ellipsizeStr, desc) //显示收缩状态的文本和图标
                } else if (ellipsizeStr.length == desc.length) {
                    tv.text = desc //正常显示Textview
                } else {
                    closeFun(tv, ellipsizeStr, desc) //显示展开状态的文本和图标
                }
                if (Build.VERSION.SDK_INT >= 16) {
                    tv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    tv.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            }
        })
    }

    // 显示收缩状态的文本，设置点击图标，并添加点击事件
     fun openFun(tv: TextView, ellipsizeStr: CharSequence, desc: String) {
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
     fun closeFun(tv: TextView, ellipsizeStr: CharSequence, desc: String) {
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