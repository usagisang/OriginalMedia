package top.gochiusa.originalmedia.video.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import top.gochiusa.glplayer.listener.EventListenerAdapter
import top.gochiusa.originalmedia.video.components.VideoScreen
import top.gochiusa.originalmedia.video.entity.VideoState


class VideoFragment : Fragment(), EventListenerAdapter {
    private val viewModel: DefaultVideoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DefaultVideoViewModel(context!!) as T
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(inflater.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                val videoItems = viewModel.getPagingVideoFlow().collectAsLazyPagingItems()
                val videoState by viewModel.videoState.collectAsState()

                LaunchedEffect(videoState) {
                    keepScreenOn = when(videoState) {
                        is VideoState.Playing -> {
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }

                VideoScreen(
                    items = videoItems,
                    videoViewModel = viewModel
                )
            }
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }
}