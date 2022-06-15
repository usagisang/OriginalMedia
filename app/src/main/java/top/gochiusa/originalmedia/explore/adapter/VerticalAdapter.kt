package top.gochiusa.originalmedia.explore.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.viewpager.widget.PagerAdapter
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.explore.bean.Data


class VerticalAdapter(var context: Context) : PagerAdapter() {
    private val mData: ArrayList<Data> = ArrayList()
    private val mContext:Context = context

    fun setData(list: ArrayList<Data>?) {
        mData.clear()
        mData.addAll(list!!)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItemPosition(@NonNull `object`: Any): Int {
        return POSITION_NONE
    }

    @NonNull
    override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {

        val view: View = View.inflate(mContext, R.layout.fragment_gra_item, null)
        container.addView(view)
        return view
    }

    override fun isViewFromObject(@NonNull view: View, @NonNull `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(@NonNull container: ViewGroup, position: Int, @NonNull `object`: Any) {
        container.removeView(`object` as View)
    }
}