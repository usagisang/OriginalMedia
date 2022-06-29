package com.kokomi.origin.explore.flow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kokomi.origin.util.emit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.player.PlayerPool
import com.kokomi.origin.util.find
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class NewsFlowFragment<VM : NewsFlowViewModel>(
    private val vm: Class<VM>,
    poolSize: Int
) : BaseFragment() {

    private lateinit var flowAdapter: NewsFlowAdapter

    private val playerPool = PlayerPool(poolSize)

    private lateinit var viewModel: VM

    private lateinit var pager2: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_news_flow_origin, container, false)
    }

    private lateinit var swipe: SwipeRefreshLayout

    private val flowCurrentItem = MutableSharedFlow<Int>(1, 0)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel(vm) {
            viewModel = this
            pager2 = view.find(R.id.vp_news_flow_pager) {
                flowAdapter = NewsFlowAdapter(
                    news.value.first,
                    playerPool,
                    lifecycle,
                    lifecycleScope,
                    flowCurrentItem
                )
                adapter = flowAdapter
                recyclerViewConfig(3) { loadMore() }
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    private var last = -1
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
//                        Log.e(TAG, "onPageSelected: $positionOffset")
//                        Log.e(TAG, "onPageSelected: $position")
                        lifecycleScope.launch {
                            if (refresh) {
                                flowCurrentItem emit position
                                refresh = false
                                pager2.isUserInputEnabled = true
                                last = position
                            } else {
                                if (last != position) {
                                    flowCurrentItem emit position
                                    last = position
                                }
                            }
                        }
                    }

                    override fun onPageSelected(position: Int) {

                    }
                })
            }
            swipe = view.find(R.id.srl_news_flow_refresh) {
                setOnRefreshListener { refresh() }
            }

            lifecycleScope.launch {
                news.collect {
                    flowAdapter.notifyDataSetChanged()
                    if (refresh) {
                        Log.i(TAG, "update - refresh")
                        pager2.setCurrentItem(0, false)
                    }
                }
            }

            refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        playerPool.resumePool()
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
        playerPool.pausePool()
    }

    private fun ViewPager2.recyclerViewConfig(size: Int, loadMore: suspend () -> Unit) {
        val field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        val recyclerView = field.get(this) as RecyclerView
        // 缓存数设置为5
        recyclerView.setItemViewCacheSize(size)
        // 预加载
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager
                if (layoutManager is LinearLayoutManager) {
                    if (dy > 0 && layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 4) {
                        lifecycleScope.launch { loadMore() }
                    }
                }
            }
        })
    }

    private var refresh = false

    internal fun refresh() {
        pager2.isUserInputEnabled = false
        swipe.isRefreshing = true
        lifecycleScope.launch {
            viewModel.refresh {
                refresh = true
                Log.i(TAG, "refresh - result = $it")
                swipe.isRefreshing = false
            }
        }
    }

    internal fun pausePlayerPool() = playerPool.pausePool()

    internal fun resumePlayerPool() = playerPool.resumePool()

    companion object {
        private const val TAG = "NewsFlowFragment"

        @JvmStatic
        inline fun <reified VM : NewsFlowViewModel> newInstance(poolSize: Int) =
            NewsFlowFragment(VM::class.java, poolSize)
    }
}