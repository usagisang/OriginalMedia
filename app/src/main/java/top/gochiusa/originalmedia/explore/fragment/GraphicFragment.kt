package top.gochiusa.originalmedia.explore.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_graphic.*
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.base.BaseFragment
import top.gochiusa.originalmedia.databinding.FragmentGraphicBinding
import top.gochiusa.originalmedia.explore.adapter.VerticalAdapter
import top.gochiusa.originalmedia.explore.bean.Data
import top.gochiusa.originalmedia.explore.viewmodel.GraphicViewModel
import top.gochiusa.originalmedia.widget.VerticalPageTransformer


class GraphicFragment : BaseFragment() {


    private val mVpGraphic by lazy { ViewModelProvider(this)[GraphicViewModel::class.java] }

    lateinit var mAdapter: VerticalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initVerticalAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_graphic, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GraphicFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }


    private fun initVerticalAdapter() {
        mAdapter = VerticalAdapter(requireContext())
        vp_Graphic.setPageTransformer(false, VerticalPageTransformer())
        vp_Graphic.adapter = mAdapter

        val list = ArrayList<Data>();
        list.add(Data(",", ArrayList(), "", "", "", ""))

        list.add(Data(",", ArrayList(), "", "", "", ""))
        list.add(Data(",", ArrayList(), "", "", "", ""))

        mAdapter.setData(list)
    }

    private fun initData() {
        mVpGraphic.graphicListLiveData.observe(viewLifecycleOwner, Observer {
            val graphicList = it.getOrNull()
            if (graphicList != null) {
                mVpGraphic.graphicList.clear()
                mVpGraphic.graphicList.addAll(graphicList)
            }
        })

//        mVpGraphic.getGraphicList("509","1")
    }


}