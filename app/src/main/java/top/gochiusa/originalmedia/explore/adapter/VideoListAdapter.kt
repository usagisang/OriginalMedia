package top.gochiusa.originalmedia.explore.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.explore.bean.Video

class VideoListAdapter(var mList: ArrayList<Video>, var mWidth: Int = 0, var mHeight: Int = 0) :
    RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = View.inflate(parent.context, R.layout.item_video, null)
        val contentView = view.findViewById<TextView>(R.id.tv_item_video_title)
        val rl = contentView.layoutParams
        val cl =  ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT)
        cl.width = mWidth
        cl.height = mHeight

        contentView.layoutParams = cl


        //创建一个viewHolder
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.mVideoTitle.text = "第$position item"
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mVideoTitle: TextView = itemView.findViewById(R.id.tv_item_video_title)
    }
}