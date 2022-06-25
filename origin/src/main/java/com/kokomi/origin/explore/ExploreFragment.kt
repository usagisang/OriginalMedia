package com.kokomi.origin.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.util.find
import com.kokomi.origin.util.statusBarHeight
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
        val image: TextView = view find R.id.tv_explore_image
        val mix: TextView = view find R.id.tv_explore_mix
        val video: TextView = view find R.id.tv_explore_video
        with(view) {
            val explorePager = find<ViewPager2>(R.id.vp_explore_pager) {
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
                currentItem = 0
            }

            image.setOnClickListener { explorePager.currentItem = 0 }

            mix.setOnClickListener { explorePager.currentItem = 1 }

            video.setOnClickListener { explorePager.currentItem = 2 }

            find<TextView>(R.id.tv_explore_status_bar) {
                height = statusBarHeight
            }

            lifecycleScope.launch {
                delay(1L)
                find<LinearLayout>(R.id.ll_explore_tab_bar) {
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