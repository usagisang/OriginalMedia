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
            0 -> NewsFlowFragment.newInstance<ImageFlowViewModel>()
            1 -> NewsFlowFragment.newInstance<MixFlowViewModel>()
            2 -> NewsFlowFragment.newInstance<VideoFlowViewModel>()
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

    /**
     * @return 若存在主播放器正在播放而被暂停，则返回被暂停的播放器的索引，
     * 若没有，则返回 -1
     * */
    internal fun tryPause(): Int {
        fragmentMap.map { if (it.value.pausePlayerPool()) return it.key }
        return -1
    }

    internal fun tryPlay(position: Int) {
        fragmentMap[position]?.playPlayerPool()
    }

    internal fun refresh(position: Int) {
        fragmentMap[position]?.refresh()
    }

}