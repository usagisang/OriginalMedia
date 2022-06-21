package top.gochiusa.originalmedia.explore.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.base.BaseFragment
import top.gochiusa.originalmedia.databinding.FragmentVisualWorldBinding


class VisualWorldFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_visual_world, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VisualWorldFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}