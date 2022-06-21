package top.gochiusa.originalmedia.explore.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.explore.bean.Graphic
import top.gochiusa.originalmedia.util.TextUtil


class VerticalAdapter(var context: Context, var loadMore: LoadMore) : PagerAdapter() {
    private val mData: ArrayList<Graphic> = ArrayList()
    private val mContext: Context = context
    private val mRoundedCorners = RoundedCorners(15)//圆角为5

    val mOptions = RequestOptions.bitmapTransform(mRoundedCorners);
    var mHasNext: Boolean = true
    fun setData(list: ArrayList<Graphic>?) {
        mData.clear()
        mData.addAll(list!!)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        println("是多少个size 请看看${mData.size}")
        return mData.size
    }

    override fun getItemPosition(@NonNull `object`: Any): Int {
        return POSITION_NONE
    }

    @NonNull
    override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {

        val view: View = View.inflate(mContext, R.layout.fragment_gra_item, null)
        val tvContent = view.findViewById<TextView>(R.id.tv_graphic_content)
        val tvTitle = view.findViewById<TextView>(R.id.tv_graphic_title)
        val tvTime = view.findViewById<TextView>(R.id.tv_time)
        val ivGraphic = view.findViewById<ImageView>(R.id.iv_graphic)

        Glide.with(context)
            .load(mData[position].images)
            .apply(mOptions)
            .into(ivGraphic)
        tvTitle.text = mData[position].title
        tvTime.text = mData[position].uploadTime
        tvContent.text = mData[position].content
        TextUtil.toggleEllipsize(tvContent, mData[position].content)
        container.addView(view)
        if (position == mData.size - 1 && mHasNext) {
            loadMore.loadMore()
        }
        return view
    }

    override fun isViewFromObject(@NonNull view: View, @NonNull `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(@NonNull container: ViewGroup, position: Int, @NonNull `object`: Any) {
        container.removeView(`object` as View)
    }
}

interface LoadMore {
    fun loadMore()
}