package top.gochiusa.originalmedia.creation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_creation.*
import kotlinx.android.synthetic.main.fragment_explore.*
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.creation.adapter.CreateAdapter
import top.gochiusa.originalmedia.explore.adapter.PageAdapter


class CreationFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_creation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTab()
    }

    

    private fun initTab() {
        val pagerAdapter = CreateAdapter(requireActivity().supportFragmentManager)
        vp_creation.adapter = pagerAdapter
        vp_creation.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tab_layout_creation
            )
        )
        tab_layout_creation.addOnTabSelectedListener(
            TabLayout.ViewPagerOnTabSelectedListener(
                vp_creation
            )
        )
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreationFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}