package top.gochiusa.originalmedia.explore.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_video.*
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.base.BaseFragment
import top.gochiusa.originalmedia.explore.adapter.VideoListAdapter
import top.gochiusa.originalmedia.explore.bean.Video


class VideoFragment : BaseFragment() {
    private lateinit var mMyAdapter: VideoListAdapter
    private val mDataList = ArrayList<Video>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VideoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (i in 0..4){
             mDataList.add(Video(1))
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        rv_video.isNestedScrollingEnabled = false
        // PagerSnapHelper
        val snapHelper: PagerSnapHelper = object : PagerSnapHelper() {
            // 在 Adapter的 onBindViewHolder 之后执行
            override fun findTargetSnapPosition(
                layoutManager: RecyclerView.LayoutManager,
                velocityX: Int,
                velocityY: Int
            ): Int {
                //  找到对应的Index

                return super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
            }

            // 在 Adapter的 onBindViewHolder 之后执行
            override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
                // TODO 找到对应的View
                return super.findSnapView(layoutManager)
            }
        }
        snapHelper.attachToRecyclerView(rv_video)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_video.layoutManager = linearLayoutManager



        // TODO 这么写是为了获取RecycleView的宽高
        rv_video.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        rv_video.viewTreeObserver.removeGlobalOnLayoutListener(this)
                    } else {
                        rv_video.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                    /**
                     * 这么写是为了获取RecycleView的宽高
                     */
                    // 创建Adapter，并指定数据集
                    mMyAdapter = VideoListAdapter(
                        mDataList,
                        rv_video.width,
                        rv_video.height
                    )
                    // 设置Adapter
                    rv_video.adapter = mMyAdapter
                }
            })
    }


}