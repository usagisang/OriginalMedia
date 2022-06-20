package top.gochiusa.originalmedia.video.fragment

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import top.gochiusa.glplayer.GLPlayer
import top.gochiusa.glplayer.base.Player
import top.gochiusa.glplayer.entity.MediaItem
import top.gochiusa.glplayer.listener.EventListenerAdapter
import top.gochiusa.originalmedia.video.data.repo.VideoRepository
import top.gochiusa.originalmedia.video.entity.Video
import top.gochiusa.originalmedia.video.entity.VideoState


class DefaultVideoViewModel(
    context: Context
): ViewModel(), EventListenerAdapter {

    val glPlayer: Player = GLPlayer.Builder(context)
        .setRenderFirstFrame(true).setInfiniteLoop(true).build()

    private val repository: VideoRepository = VideoRepository()

    private val _videoState: MutableStateFlow<VideoState> = MutableStateFlow(VideoState.Loading)
    val videoState: StateFlow<VideoState> = _videoState

    private val _sliderValue: MutableState<Float> = mutableStateOf(0F)
    val sliderValue: State<Float> = _sliderValue

    /**
     * 标记是否正在拖动进度条
     */
    private val _dragState: MutableState<Boolean> = mutableStateOf(false)
    val dragState = _dragState

    /**
     * 发送video当前的进度的flow
     */
    val progressFlow = flow {
        while (true) {
            _videoState.value.run {
                // 只有在状态为正在播放、未拖动时才更新进度
                if (this is VideoState.Playing && !_dragState.value) {
                    emit(glPlayer.currentPositionMs.coerceAtLeast(0L))
                }
            }
            delay(200L)
        }
    }.flowOn(Dispatchers.IO).conflate()

    val duration: Long
        get() = glPlayer.durationMs.coerceAtLeast(0L)

    init {
        glPlayer.addEventListener(this)
    }

    override fun onPlayerError(errorCode: Int) {
        if (errorCode == GLPlayer.SOURCE_ERROR) {
            _videoState.value = VideoState.Error
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when(playbackState) {
            Player.STATE_READY -> {
                _videoState.value = VideoState.Playing
                glPlayer.play()
            }
            Player.STATE_BUFFERING -> {
                _videoState.value = VideoState.Loading
            }
            Player.STATE_PLAYING -> {
                if (_videoState.value !is VideoState.Playing) {
                    _videoState.value = VideoState.Playing
                }
            }
            Player.STATE_PAUSE -> {
                if (_videoState.value is VideoState.Playing) {
                    _videoState.value = VideoState.Pause
                }
            }
        }
    }

    fun onSliderDrag(progress: Float) {
        _dragState.value = true
        _sliderValue.value = progress
    }

    fun onSliderDragFinish() {
        _dragState.value = false
        glPlayer.seekTo(_sliderValue.value.toLong())
    }

    fun changeSliderValueUncheck(progress: Float) {
        _sliderValue.value = progress
    }

    fun play() {
        if (_videoState.value is VideoState.Pause) {
            _videoState.value = VideoState.Playing
            glPlayer.play()
        }
    }

    fun pause() {
        if (_videoState.value is VideoState.Playing) {
            _videoState.value = VideoState.Pause
            glPlayer.pause()
        }
    }

    fun onPageChange(video: Video?) {
        glPlayer.pause()
        if (_videoState.value !is VideoState.Loading) {
            _videoState.value = VideoState.Loading
        }
        changeSliderValueUncheck(0F)
        video?.apply {
            glPlayer.setMediaItem(MediaItem.fromUrl(videoUrl))
            glPlayer.prepare()
        }
    }

    fun onSurfaceClick() {
        if (_videoState.value is VideoState.Playing) {
            _videoState.value = VideoState.Pause
            glPlayer.pause()
        } else if (_videoState.value is VideoState.Pause) {
            _videoState.value = VideoState.Playing
            glPlayer.play()
        }
    }

    fun getPagingVideoFlow() = repository.getPagingVideoFlow()


    override fun onCleared() {
        super.onCleared()
        glPlayer.addEventListener(this)
        glPlayer.release()
    }
}