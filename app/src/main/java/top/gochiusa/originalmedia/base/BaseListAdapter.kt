package top.gochiusa.originalmedia.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import top.gochiusa.originalmedia.widget.LoadMoreView

abstract class BaseListAdapter <ITEM_BEAN, ITEM_VIEW : View>:
    RecyclerView.Adapter<BaseListAdapter.BaseListHolder>(){
    private var mList = ArrayList<ITEM_BEAN>()
    private var mListener: ((itemBean: ITEM_BEAN) -> Unit)? = null
    abstract fun refreshItemView(itemView: ITEM_VIEW, data: ITEM_BEAN)
    abstract fun getItemView(context: Context?): ITEM_VIEW

    /**
     *  更新数据
     */
    fun updateList(list: List<ITEM_BEAN>?) {
        list?.let {
            this.mList.clear()
            this.mList.addAll(list)
            notifyDataSetChanged()

        }
    }

    /**
     * 加载更多
     */
    fun loadMore(list: List<ITEM_BEAN>?) {
        list?.let {
            this.mList.addAll(list)
            notifyDataSetChanged()
        }
    }

    class BaseListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseListHolder {
        if (viewType == 1) {
            //最后一条
            return BaseListHolder(LoadMoreView(parent?.context))
        } else {
            //普通条目
            return BaseListHolder(getItemView(parent?.context))
        }    }

    override fun onBindViewHolder(holder: BaseListHolder, position: Int) {
         if (position == mList.size) return
        val data = mList[position]
        val itemView = holder?.itemView as ITEM_VIEW
        //条目刷新
        refreshItemView(itemView, data)


        itemView.setOnClickListener {
            mListener?.let {
                it(data)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size + 1
    }


}