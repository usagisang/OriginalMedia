package com.kokomi.origin.explore.flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.player.PlayerPool
import com.kokomi.origin.util.find
import kotlinx.coroutines.launch

class NewsFlowFragment<VM : NewsFlowViewModel>(
    private val vm: Class<VM>,
) : BaseFragment() {

    private lateinit var flowAdapter: NewsFlowAdapter

    private val playerPool = PlayerPool(5)

    private var shouldResumePlay = false

    private lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var pager2: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_news_flow_origin, container, false)
    }

    private lateinit var swipe: SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel(vm) {
            viewModel = this
            pager2 = view.find(R.id.vp_news_flow_pager) {
                flowAdapter = NewsFlowAdapter(
                    news.value.first,
                    playerPool,
                    lifecycle
                ) { lifecycleScope.launch { loadMore() } }
                adapter = flowAdapter
                setViewPager2CacheSize(5)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        flowCurrentItem = position
                    }
                })
            }
            swipe = view.find(R.id.srl_news_flow_refresh) {
                setOnRefreshListener {
                    lifecycleScope.launch { refresh { isRefreshing = false } }
                }
            }

            lifecycleScope.launch {
                news.collect {
                    flowAdapter.notifyDataSetChanged()
                }
            }

            refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        if (shouldResumePlay) playerPool.mainPlayerPlay()
    }

    override fun onPause() {
        super.onPause()
        shouldResumePlay = playerPool.mainPlayerPause()
    }

    private fun ViewPager2.setViewPager2CacheSize(size: Int) {
        val field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        val recyclerView = field.get(this) as RecyclerView
        recyclerView.setItemViewCacheSize(size)
    }

    internal fun refresh() {
        pager2.setCurrentItem(0, false)
        swipe.isRefreshing = true
        lifecycleScope.launch {
            viewModel.refresh { swipe.isRefreshing = false }
        }
    }

    internal fun pausePlayerPool() = playerPool.mainPlayerPause()

    internal fun playPlayerPool() = playerPool.mainPlayerPlay()

    companion object {
        @JvmStatic
        inline fun <reified VM : NewsFlowViewModel> newInstance() =
            NewsFlowFragment(VM::class.java)
    }
}