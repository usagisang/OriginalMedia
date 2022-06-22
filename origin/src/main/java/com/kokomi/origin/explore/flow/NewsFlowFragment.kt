package com.kokomi.origin.explore.flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.view
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
            view.view<ViewPager2>(R.id.vp_news_flow_pager) {
                imageFlowAdapter = NewsFlowAdapter(news.value.first) {
                    lifecycleScope.launch { loadMore() }
                }
                adapter = imageFlowAdapter
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        flowCurrentItem = position
                        imageFlowAdapter.notifyItemChanged(position)
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

    companion object {
        @JvmStatic
        inline fun <reified VM : NewsFlowViewModel> newInstance() =
            NewsFlowFragment(VM::class.java)
    }
}