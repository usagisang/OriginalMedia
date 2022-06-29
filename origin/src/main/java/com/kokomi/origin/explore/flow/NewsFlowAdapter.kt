package com.kokomi.origin.explore.flow

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kokomi.origin.R
import com.kokomi.origin.entity.News
import com.kokomi.origin.entity.TYPE_IMAGE
import com.kokomi.origin.explore.tabBarHeight
import com.kokomi.origin.navigationHeight
import com.kokomi.origin.player.PlayerPool
import com.kokomi.origin.util.*
import com.kokomi.origin.weight.OriginScrollView
import com.kokomi.origin.weight.PlayerSwipeSlider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import top.gochiusa.glplayer.PlayerView
import top.gochiusa.glplayer.base.Player

private const val OPEN_TEXT = "展开"
private const val EMPTY_TEXT = ""
private const val CLOSE_TEXT = "收起"
private const val DATE_SUFFIX = "发布时间  "

internal class NewsFlowAdapter(
    private val news: List<News>,
    private val playerPool: PlayerPool,
    private val lifecycle: Lifecycle,
    private val lifecycleScope: CoroutineScope,
    private val flowCurrentItem: StateFlow<Pair<Int, Boolean>>
) : RecyclerView.Adapter<NewsFlowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_IMAGE) {
            ViewHolderImageImpl(
                playerPool,
                lifecycleScope,
                flowCurrentItem,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image_news_origin, parent, false)
            )
        } else {
            ViewHolderVideoImpl(
                playerPool,
                lifecycle,
                lifecycleScope,
                flowCurrentItem,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_video_news_origin, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindViewHolder(news[position], position)
    }

    override fun getItemViewType(position: Int) = news[position].type

    override fun getItemCount() = news.size

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        holder.onAttached()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.onDetached()
    }

    internal abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        internal abstract fun onBindViewHolder(new: News, position: Int)

        internal abstract fun onDetached()

        internal abstract fun onAttached()
    }

    internal class ViewHolderImageImpl(
        private val playerPool: PlayerPool,
        lifecycleScope: CoroutineScope,
        flowCurrentItem: StateFlow<Pair<Int, Boolean>>,
        root: View
    ) : ViewHolder(root) {
        private val scroll = root.find<OriginScrollView>(R.id.sv_image_news_scroll)
        private val title = root.find<TextView>(R.id.tv_image_news_title)
        private val image = root.find<ImageView>(R.id.iv_image_news_image)
        private val content = root.find<TextView>(R.id.tv_image_news_content)
        private val openLayout = root.find<View>(R.id.ll_image_news_open)
        private val open = root.find<TextView>(R.id.tv_image_news_open)
        private val openIcon = root.find<ImageView>(R.id.iv_image_news_open_icon)
        private val publishTime = root.find<TextView>(R.id.tv_image_news_publish_time)

        init {
            root.find<TextView>(R.id.tv_image_news_status_bar) {
                lifecycleScope.launch {
                    tabBarHeight.collect { height = it + root.context.statusBarHeight }
                }
            }
            root.find<TextView>(R.id.tv_image_news_navigation) {
                lifecycleScope.launch {
                    navigationHeight.collect { height = it }
                }
            }
            lifecycleScope.launch {
                flowCurrentItem.collect {
                    if (it.first == adapterPosition) {
                        playerPool exchange null
                    }
                }
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
            openLayout.setOnClickListener {
                if (open.text == OPEN_TEXT) {
                    open()
                } else if (open.text == CLOSE_TEXT) {
                    close()
                }
            }
            publishTime.text = getFormatDate(DATE_SUFFIX, new.uploadTime)
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

        override fun onDetached() {
            close()
        }

        override fun onAttached() {
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

    internal class ViewHolderVideoImpl(
        private val playerPool: PlayerPool,
        lifecycle: Lifecycle,
        lifecycleScope: CoroutineScope,
        flowCurrentItem: StateFlow<Pair<Int, Boolean>>,
        root: View
    ) : ViewHolder(root) {
        private val title = root.find<TextView>(R.id.tv_video_news_title)
        private val playerView = root.find<PlayerView>(R.id.pv_video_news_player)
        private val start = root.find<ImageView>(R.id.iv_video_news_start)
        private val publishTime = root.find<TextView>(R.id.tv_video_news_publish_time)
        private val slider = root.find<PlayerSwipeSlider>(R.id.slider_video_news_progress)
        private val progress = root.find<TextView>(R.id.tv_video_news_progress)

        init {
            root.find<TextView>(R.id.tv_video_news_status_bar) {
                lifecycleScope.launch {
                    tabBarHeight.collect { height = it + root.context.statusBarHeight }
                }
            }
            root.find<TextView>(R.id.tv_video_news_navigation) {
                lifecycleScope.launch {
                    navigationHeight.collect { height = it }
                }
            }
            slider.setOnDragSliderListener { end, value, duration ->
                if (duration != null) {
                    progress.text = "${toTimeText(value * duration)}  /  ${toTimeText(duration)}"
                    progress.visibility = if (end) View.GONE else View.VISIBLE
                }
            }
            playerView.bindLifecycle(lifecycle)
            playerView.setOnClickListener {
                playerView.bindPlayer?.let { player ->
                    if (player.playerState == Player.STATE_PLAYING) {
                        player.pause()
                        start.visibility = View.VISIBLE
                    } else if (player.playerState == Player.STATE_PAUSE) {
                        player.play()
                        start.visibility = View.GONE
                    }
                }
            }
            lifecycleScope.launch {
                flowCurrentItem.collect {
                    if (it.first == adapterPosition) {
                        playerPool exchange playerView.bindPlayer
                        playerView.onResume()
                    } else {
                        playerPool.pauseMain(playerView.bindPlayer)
                        playerView.onPause()
                    }
                }
            }
        }

        override fun onBindViewHolder(new: News, position: Int) {
            playerPool.prepare(playerView, new.resource)
            slider.bindPlayer(playerView.bindPlayer)
            title.text = new.title
            publishTime.text = getFormatDate(DATE_SUFFIX, new.uploadTime)
        }

        override fun onDetached() {
        }

        override fun onAttached() {
        }
    }

}