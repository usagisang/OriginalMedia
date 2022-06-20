package top.gochiusa.originalmedia.video.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collectLatest
import top.gochiusa.glplayer.PlayerView
import top.gochiusa.originalmedia.video.entity.Video
import top.gochiusa.originalmedia.video.fragment.DefaultVideoViewModel
import top.gochiusa.originalmedia.video.util.ProgressUtil


@ExperimentalPagerApi
@Composable
internal fun VideoScreen(
    items: LazyPagingItems<Video>,
    pagerState: PagerState = rememberPagerState(),
    videoViewModel: DefaultVideoViewModel
) {
    val currentPage by remember {
        snapshotFlow {
            pagerState.currentPage
        }
    }.collectAsState(initial = 0)

    // 初始状态下为player提供数据的Effect
    LaunchedEffect(items.itemCount > 0) {
        if (items.itemCount > 0) {
            videoViewModel.onPageChange(items[0])
        }
    }
    // 上拉/下滑到新的一页的处理逻辑
    LaunchedEffect(currentPage) {
        videoViewModel.onPageChange(if (items.itemCount <= currentPage) null else
            items[currentPage])
    }


    VerticalPager(
        count = items.itemCount,
        state = pagerState,
        modifier = Modifier.background(color = Color.Black)
    ) { page ->
        if (currentPage == page) {
            VideoPagerContent(video = items[page], videoViewModel = videoViewModel)
        } else {
            EmptyPagerContent(video = items[page])
        }
    }
}


@Composable
internal fun VideoPagerContent(
    video: Video?,
    videoViewModel: DefaultVideoViewModel
) {
    LaunchedEffect(true) {
        videoViewModel.progressFlow.collectLatest {
            videoViewModel.changeSliderValueUncheck(it.toFloat())
        }
    }
    val videoState by videoViewModel.videoState.collectAsState()

    val range by remember(videoViewModel.duration) {
        mutableStateOf(0F..videoViewModel.duration.toFloat())
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        VideoTitle(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 5.dp),
            video = video)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        setPlayer(videoViewModel.glPlayer)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clickableNoIndication(videoViewModel::onSurfaceClick)
            )
            if (videoViewModel.dragState.value) {
                ProgressText(
                    progress = ProgressUtil.toTimeText(videoViewModel.sliderValue.value),
                    duration = ProgressUtil.toTimeText(videoViewModel.duration),
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            VideoStateComponents(
                modifier = Modifier.align(Alignment.Center),
                videoState = videoState,
                onRetryClick = {
                    videoViewModel.onPageChange(video)
                }
            )
        }

        Text(
            text = "发布日期  ${ProgressUtil.getFormatDate(video?.uploadTime)}",
            color = Color(0x66F5F5F5),
            fontSize = 15.sp,
            letterSpacing = 0.5.sp,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 10.dp, top = 3.dp, bottom = 3.dp)
        )

        SwipeSlider(
            value = videoViewModel.sliderValue.value,
            onValueChange = videoViewModel::onSliderDrag,
            onValueChangeFinished = videoViewModel::onSliderDragFinish,
            modifier = Modifier.fillMaxWidth(),
            valueRange = range,
            color = Color.White
        )
    }
}

@Composable
private fun EmptyPagerContent(
    video: Video?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        VideoTitle(modifier = Modifier.align(Alignment.TopCenter), video = video)
    }
}


