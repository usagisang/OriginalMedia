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
) : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var imageFlowAdapter: NewsFlowAdapter

    private val playerPool = PlayerPool(5)

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
                    playerPool
                ) { lifecycleScope.launch { loadMore() } }
                adapter = imageFlowAdapter
                setViewPager2CacheSize(5)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        flowCurrentItem = position
                    }
                })
            }
            view.find<SwipeRefreshLayout>(R.id.srl_news_flow_refresh) {
                setOnRefreshListener {
                    lifecycleScope.launch {
                        this@viewModel.apply {
                            reset()
                            loadMore()
                        }
                        this@find.isRefreshing = false
                    }
                }
            }

            lifecycleScope.launch {
                news.collect {
                    imageFlowAdapter.notifyDataSetChanged()
                }
            }

            lifecycleScope.launch { loadMore() }
        }
    }

    override fun onRefresh() {

    }

    private fun ViewPager2.setViewPager2CacheSize(size: Int) {
        val field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        val recyclerView = field.get(this) as RecyclerView
        recyclerView.setItemViewCacheSize(size)
    }

    internal fun pausePlayerPool() = playerPool.mainPlayerPause()

    internal fun playPlayerPool() = playerPool.mainPlayerPlay()

    companion object {
        @JvmStatic
        inline fun <reified VM : NewsFlowViewModel> newInstance() =
            NewsFlowFragment(VM::class.java)
    }
}