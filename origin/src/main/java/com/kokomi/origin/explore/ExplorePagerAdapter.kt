package com.kokomi.origin.explore

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.kokomi.origin.explore.flow.*

internal class ExplorePagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val fragmentMap = hashMapOf<Int, NewsFlowFragment<*>>()

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NewsFlowFragment.newInstance<ImageFlowViewModel>(0)
            1 -> NewsFlowFragment.newInstance<MixFlowViewModel>(5)
            2 -> NewsFlowFragment.newInstance<VideoFlowViewModel>(5)
            else -> throw IllegalStateException()
        }.also { fragmentMap[position] = it }
    }

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
        pausePlayerPool(holder)
    }

    private fun pausePlayerPool(holder: FragmentViewHolder) {
        fragmentMap[holder.adapterPosition]!!.pausePlayerPool()
    }

    internal fun tryPause() {
        fragmentMap.map { it.value.pausePlayerPool() }
    }

    internal fun tryResume() {
        fragmentMap.map { it.value.resumePlayerPool() }
    }

    internal fun refresh(position: Int) {
        fragmentMap[position]?.refresh()
    }

}