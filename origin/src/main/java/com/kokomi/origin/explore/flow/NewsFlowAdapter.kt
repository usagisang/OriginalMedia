package com.kokomi.origin.explore.flow

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kokomi.origin.*
import com.kokomi.origin.entity.News
import com.kokomi.origin.entity.TYPE_IMAGE
import com.kokomi.origin.explore.tabBarHeight
import com.kokomi.origin.util.getStatusBarHeight
import com.kokomi.origin.util.html
import com.kokomi.origin.player.GLPlayer
import com.kokomi.origin.player.glPlayer
import com.kokomi.origin.util.view
import com.kokomi.origin.weight.OriginScrollView
import top.gochiusa.glplayer.PlayerView

internal var flowCurrentItem = -1

private const val OPEN_TEXT = "展开"
private const val EMPTY_TEXT = ""
private const val CLOSE_TEXT = "收起"

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
        holder.onBindViewHolder(news[position], position)
        if (position == news.size - 3 || position == news.size - 1) loadMore()
    }

    override fun getItemViewType(position: Int) = news[position].type

    override fun getItemCount() = news.size

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.onViewHolderDetached()
    }

    internal abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        abstract fun onBindViewHolder(new: News, position: Int)

        abstract fun onViewHolderDetached()
    }

    internal class ViewHolderImageImpl(root: View) : ViewHolder(root) {
        private val scroll = root.view<OriginScrollView>(R.id.sv_image_news_scroll)
        private val title = root.view<TextView>(R.id.tv_image_news_title)
        private val image = root.view<ImageView>(R.id.iv_image_news_image)
        private val content = root.view<TextView>(R.id.tv_image_news_content)
        private val open = root.view<TextView>(R.id.tv_image_news_open)
        private val openIcon = root.view<ImageView>(R.id.iv_image_news_open_icon)

        init {
            root.view<TextView>(R.id.tv_image_news_status_bar) {
                height = root.context.getStatusBarHeight() + tabBarHeight
            }
            root.view<TextView>(R.id.tv_image_news_navigation) {
                height = navigationHeight
            }
        }

        override fun onBindViewHolder(new: News, position: Int) {
            Glide.with(image)
                .load(new.resource)
                .into(image)
            title.text = new.title
            content.text = new.content.html
            scroll.scrollTo(0, 0)
            scroll.isScrollable = false
            open.setOnClickListener {
                if (open.text == OPEN_TEXT) {
                    open()
                } else if (open.text == CLOSE_TEXT) {
                    close()
                }
            }
            scroll.postDelayed({
                val canScroll = scroll.canScroll
                open.text = if (canScroll) {
                    openIcon.rotation = 0f
                    openIcon.visibility = View.VISIBLE
                    open.visibility = View.VISIBLE
                    OPEN_TEXT
                } else {
                    openIcon.visibility = View.GONE
                    open.visibility = View.GONE
                    EMPTY_TEXT
                }
            }, 1L)
        }

        override fun onViewHolderDetached() {
            close()
        }

        private fun open() {
            scroll.isScrollable = true
            open.text = CLOSE_TEXT
            ObjectAnimator.ofFloat(
                openIcon,
                "rotation",
                openIcon.rotation,
                -180f
            ).apply {
                setAutoCancel(true)
            }.start()
        }

        private fun close() {
            scroll.isScrollable = false
            open.text = OPEN_TEXT
            scroll.smoothScrollTo(0, 0)
            ObjectAnimator.ofFloat(
                openIcon,
                "rotation",
                openIcon.rotation,
                0f
            ).apply {
                setAutoCancel(true)
            }.start()
        }
    }

    internal class ViewHolderVideoImpl(root: View) : ViewHolder(root) {
        private val title = root.view<TextView>(R.id.tv_video_news_title)
        private val player = root.view<PlayerView>(R.id.pv_video_news_player)

        init {
            root.view<TextView>(R.id.tv_video_news_status_bar) {
                height = root.context.getStatusBarHeight() + tabBarHeight
            }
            root.view<TextView>(R.id.tv_video_news_navigation) {
                height = navigationHeight
            }
        }

        override fun onBindViewHolder(new: News, position: Int) {
            if (flowCurrentItem == position) {
                player.onResume()
                player.setPlayer(glPlayer)
                GLPlayer.play(new.resource)
            }
            title.text = new.title
        }

        override fun onViewHolderDetached() {
            player.onPause()
        }
    }

}