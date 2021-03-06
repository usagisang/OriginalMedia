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
import com.kokomi.origin.util.emit
import com.kokomi.origin.util.find
import com.kokomi.origin.util.statusBarHeight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val _tabBarHeight = MutableStateFlow(0)
internal val tabBarHeight: StateFlow<Int> = _tabBarHeight

private const val COLOR_SELECTED = 0xFFF5F5F5.toInt()
private const val COLOR_UN_SELECTED = 0xCCBDBDBD.toInt()

class ExploreFragment : BaseFragment() {

    private var exploreAdapter: ExplorePagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_explore_origin, container, false)
    }

    private lateinit var tabBar: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val image: TextView = view find R.id.tv_explore_image
        val mix: TextView = view find R.id.tv_explore_mix
        val video: TextView = view find R.id.tv_explore_video
        tabBar = view find R.id.ll_explore_tab_bar

        with(view) {
            val explorePager = find<ViewPager2>(R.id.vp_explore_pager) {
                exploreAdapter = ExplorePagerAdapter(this@ExploreFragment)
                adapter = exploreAdapter
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

            image.setOnClickListener {
                if (explorePager.currentItem == 0) {
                    exploreAdapter?.refresh(0)
                } else explorePager.currentItem = 0
            }

            mix.setOnClickListener {
                if (explorePager.currentItem == 1) {
                    exploreAdapter?.refresh(1)
                } else explorePager.currentItem = 1
            }

            video.setOnClickListener {
                if (explorePager.currentItem == 2) {
                    exploreAdapter?.refresh(2)
                } else explorePager.currentItem = 2
            }

            find<TextView>(R.id.tv_explore_status_bar) {
                height = statusBarHeight
            }

            tabBar.viewTreeObserver
                .addOnGlobalLayoutListener {
                    lifecycleScope.launch { _tabBarHeight emit tabBar.height }
                }

        }
    }

    internal fun onHide() {
        exploreAdapter?.tryPause()
    }

    internal fun onShow() {
        exploreAdapter?.tryResume()
    }

    private fun TextView.isSelected(selected: Boolean) {
        setTextColor(if (selected) COLOR_SELECTED else COLOR_UN_SELECTED)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExploreFragment()
    }
}