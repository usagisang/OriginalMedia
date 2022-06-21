package top.gochiusa.originalmedia.explore.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_explore.*
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.base.BaseFragment
import top.gochiusa.originalmedia.databinding.FragmentExploreBinding
import top.gochiusa.originalmedia.explore.adapter.PageAdapter


class ExploreFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTab()
    }

    private fun initTab() {
        val pagerAdapter = PageAdapter(requireActivity().supportFragmentManager)
            viewpager.adapter = pagerAdapter
            viewpager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    tab_layout
                )
            )
            tab_layout.addOnTabSelectedListener(
                TabLayout.ViewPagerOnTabSelectedListener(
                    viewpager
                )
            )

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExploreFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }


}