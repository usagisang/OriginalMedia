package com.kokomi.origin.explore

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kokomi.origin.explore.flow.*

internal class ExplorePagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NewsFlowFragment.newInstance<ImageFlowViewModel>()
            1 -> NewsFlowFragment.newInstance<MixFlowViewModel>()
            2 -> NewsFlowFragment.newInstance<VideoFlowViewModel>()
            else -> TODO()
        }
    }

}