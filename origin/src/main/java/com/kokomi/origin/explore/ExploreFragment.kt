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
import com.kokomi.origin.getStatusBarHeight
import com.kokomi.origin.view
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal var tabBarHeight = 0

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
            val explorePager = view<ViewPager2>(R.id.vp_explore_pager) {
                adapter = ExplorePagerAdapter(this@ExploreFragment)
            }

            view<TextView>(R.id.tv_explore_image) {
                setOnClickListener { explorePager.currentItem = 0 }
            }

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

    companion object {
        @JvmStatic
        fun newInstance() = ExploreFragment()
    }
}