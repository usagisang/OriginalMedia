package com.kokomi.origin.explore.flow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kokomi.origin.*
import com.kokomi.origin.entity.News
import com.kokomi.origin.entity.TYPE_IMAGE
import com.kokomi.origin.explore.tabBarHeight
import com.kokomi.origin.getStatusBarHeight
import com.kokomi.origin.html
import com.kokomi.origin.view

internal class NewsFlowAdapter(
    private val news: List<News>,
    private val loadMore: () -> Unit
) : RecyclerView.Adapter<NewsFlowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_IMAGE) {
            ViewHolderImageImpl(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image_news_origin, parent, false)
            )
        } else {
            ViewHolderVideoImpl(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_video_news_origin, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val new = news[position]
        if (new.type == TYPE_IMAGE)
            with(holder as ViewHolderImageImpl) {
                Glide.with(image)
                    .load(new.resource)
                    .into(image)
                scroll.scrollTo(0, 0)
                title.text = new.title
                content.text = new.content.html
            }
        else {
            with(holder as ViewHolderVideoImpl) {
//                player.setPlayer()
                title.text = new.title
            }
        }
        if (position == news.size - 3 || position == news.size - 1) loadMore()
    }

    override fun getItemViewType(position: Int) = news[position].type

    override fun getItemCount() = news.size

    internal abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root)

    internal class ViewHolderImageImpl(root: View) : ViewHolder(root) {
        internal val scroll = root.view<ScrollView>(R.id.sv_image_news_scroll)
        internal val title = root.view<TextView>(R.id.tv_image_news_title)
        internal val image = root.view<ImageView>(R.id.iv_image_news_image)
        internal val content = root.view<TextView>(R.id.tv_image_news_content)

        init {
            root.view<TextView>(R.id.tv_image_news_status_bar) {
                height = root.context.getStatusBarHeight() + tabBarHeight
            }
            root.view<TextView>(R.id.tv_image_news_navigation) {
                height = navigationHeight
            }
        }
    }

    internal class ViewHolderVideoImpl(root: View) : ViewHolder(root) {
        internal val title = root.view<TextView>(R.id.tv_video_news_title)
//        internal val player = root.view<PlayerView>(R.id.pv_video_news_player)

        init {
            root.view<TextView>(R.id.tv_video_news_status_bar) {
                height = root.context.getStatusBarHeight() + tabBarHeight
            }
            root.view<TextView>(R.id.tv_video_news_navigation) {
                height = navigationHeight
            }
        }
    }

}