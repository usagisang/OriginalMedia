package com.kokomi.origin.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.base.ModelFragment
import com.kokomi.origin.util.find
import com.kokomi.origin.util.statusBarHeight

private const val COLOR_SELECTED = 0xFFF5F5F5.toInt()
private const val COLOR_UN_SELECTED = 0xCCBDBDBD.toInt()

class CreationFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_creation_origin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val image: TextView = view find R.id.tv_creation_image
        val start: TextView = view find R.id.tv_creation_start
        val video: TextView = view find R.id.tv_creation_video
        with(view) {
            find<ViewPager2>(R.id.vp_creation_pager) {
                adapter = CreationPagerAdapter(this@CreationFragment) {
                    currentItem = it
                }
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        when (position) {
                            0 -> {
                                image.isSelected(true)
                                start.isSelected(false)
                                video.isSelected(false)
                            }
                            1 -> {
                                image.isSelected(false)
                                start.isSelected(true)
                                video.isSelected(false)
                            }
                            2 -> {
                                image.isSelected(false)
                                start.isSelected(false)
                                video.isSelected(true)
                            }
                        }
                    }
                })
                setCurrentItem(1, false)

                image.setOnClickListener { currentItem = 0 }

                start.setOnClickListener { currentItem = 1 }

                video.setOnClickListener { currentItem = 2 }
            }

            find<TextView>(R.id.tv_creation_status_bar) {
                height = statusBarHeight
            }
        }
    }

    private fun TextView.isSelected(selected: Boolean) {
        setTextColor(if (selected) COLOR_SELECTED else COLOR_UN_SELECTED)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ModelFragment()
    }

}