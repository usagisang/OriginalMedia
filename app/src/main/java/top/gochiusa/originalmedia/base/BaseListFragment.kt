package top.gochiusa.originalmedia.base

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import top.gochiusa.originalmedia.databinding.FragmentListBinding

/*

*/
/**
 * ClassName:BaseListFragment
 * Description:所有具有下拉刷新和上拉加载更多列表界面的基类
 * 基类抽取
 * HomeView->BaseView
 * Presenter->BaseListPresenter
 * Adapter->BaseListAdapter
 *//*

abstract class BaseListFragment<RESPONSE, ITEM_BEAN, ITEM_VIEW :
View> : BaseView<RESPONSE>, BaseFragment<FragmentListBinding>() {
    private val adapter by lazy { getSpecialAdapter() }

    */
/**
     * 获取适配器adapter
     *//*

    abstract fun getSpecialAdapter(): BaseListAdapter<ITEM_BEAN, ITEM_VIEW>
    override fun onError(message: String?) {
        mBinding?. refreshLayout?.isRefreshing = false

    }

    override fun loadSuccess(reponse: RESPONSE?) {
        //隐藏刷新控件
        mBinding?.refreshLayout?.isRefreshing = false
        //刷新列表
        adapter.updateList(getList(reponse))
    }

    override fun loadMore(response: RESPONSE?) {
        adapter.loadMore(getList(response))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initData()
    }


    private fun initListener(){
        mBinding?.apply {
            recycleView.layoutManager = LinearLayoutManager(context)
            recycleView.adapter = adapter
            //初始化刷新控件
            refreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN)
            //刷新监听
            refreshLayout.setOnRefreshListener {
                //loadData() 加载更加多的动作
                //todo
            }
            recycleView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        //看看是否是最后一条
                        val layoutManager = recyclerView.layoutManager
                        if (layoutManager is LinearLayoutManager){
                            val manager:LinearLayoutManager =layoutManager
                            val lastPosition = manager.findLastVisibleItemPosition()
                            if (lastPosition == adapter.itemCount -1 ){
                                //加载更加多 todo
                            }
                        }
                    }
                }
            })

        }
    }

    private fun initData(){
        //初始化数据  todo
    }

    abstract fun getList(response: RESPONSE?): List<ITEM_BEAN>?

}*/
