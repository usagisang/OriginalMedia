package com.kokomi.origin.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.util.getStatusBarHeight
import com.kokomi.origin.util.view
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal var tabBarHeight = 0

private const val COLOR_SELECTED = 0xFFF5F5F5.toInt()
private const val COLOR_UN_SELECTED = 0xCCBDBDBD.toInt()

class ExploreFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_explore_origin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view) {
            val image = view<TextView>(R.id.tv_explore_image)
            val mix = view<TextView>(R.id.tv_explore_mix)
            val video = view<TextView>(R.id.tv_explore_video)

            val explorePager = view<ViewPager2>(R.id.vp_explore_pager) {
                adapter = ExplorePagerAdapter(this@ExploreFragment)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        when (position) {
                            0 -> {
                                image.isSelected(true)
                                mix.isSelected(false)
                                video.isSelected(false)
                            }
                            1 -> {
                                image.isSelected(false)
                                mix.isSelected(true)
                                video.isSelected(false)
                            }
                            2 -> {
                                image.isSelected(false)
                                mix.isSelected(false)
                                video.isSelected(true)
                            }
                        }
                    }
                })
            }

            image.setOnClickListener { explorePager.currentItem = 0 }

            view<TextView>(R.id.tv_explore_mix) {
                setOnClickListener { explorePager.currentItem = 1 }
            }

            view<TextView>(R.id.tv_explore_video) {
                setOnClickListener { explorePager.currentItem = 2 }
            }

            view<TextView>(R.id.tv_explore_status_bar) {
                height = getStatusBarHeight()
            }

            lifecycleScope.launch {
                delay(5L)
                view<LinearLayout>(R.id.ll_explore_tab_bar) {
                    tabBarHeight = height
                }
            }
        }
    }

    private fun TextView.isSelected(selected: Boolean) {
        setTextColor(if (selected) COLOR_SELECTED else COLOR_UN_SELECTED)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExploreFragment()
    }
}