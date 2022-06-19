package top.gochiusa.originalmedia.widget

import android.view.View
import androidx.viewpager.widget.ViewPager


class VerticalPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        if (position >= -1 && position <= 1) {
            view.translationX = view.width * -position
            val yPosition: Float = position * view.height
            view.translationY = yPosition
        }
    }
}