package com.kokomi.origin.explore.flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.player.PlayerPool
import com.kokomi.origin.util.find
import kotlinx.coroutines.launch

class NewsFlowFragment<VM : NewsFlowViewModel>(
    private val vm: Class<VM>
) : BaseFragment() {

    private lateinit var imageFlowAdapter: NewsFlowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_news_flow_origin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel(vm) {
            view.find<ViewPager2>(R.id.vp_news_flow_pager) {
                imageFlowAdapter = NewsFlowAdapter(
                    news.value.first,
                    PlayerPool()
                ) { lifecycleScope.launch { loadMore() } }
                adapter = imageFlowAdapter
                setViewPager2CacheSize(5)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        flowCurrentItem = position
                    }
                })
            }

            lifecycleScope.launch {
                news.collect {
                    imageFlowAdapter.notifyDataSetChanged()
                }
            }

            lifecycleScope.launch { loadMore() }
        }
    }

    private fun ViewPager2.setViewPager2CacheSize(size: Int) {
        val field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        val recyclerView = field.get(this) as RecyclerView
        recyclerView.setItemViewCacheSize(size)
    }

    companion object {
        @JvmStatic
        inline fun <reified VM : NewsFlowViewModel> newInstance() =
            NewsFlowFragment(VM::class.java)
    }
}