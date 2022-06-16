package top.gochiusa.glplayer

import android.content.Context
import android.os.*
import android.view.Surface
import android.view.SurfaceView
import top.gochiusa.glplayer.base.Player
import top.gochiusa.glplayer.base.Renderer
import top.gochiusa.glplayer.base.RendererFactory
import top.gochiusa.glplayer.base.SurfaceProvider
import top.gochiusa.glplayer.data.DefaultMediaSourceFactory
import top.gochiusa.glplayer.data.MediaSource
import top.gochiusa.glplayer.data.MediaSourceFactory
import top.gochiusa.glplayer.data.SeekMode
import top.gochiusa.glplayer.entity.Format
import top.gochiusa.glplayer.entity.MediaItem
import top.gochiusa.glplayer.listener.EventListener
import top.gochiusa.glplayer.listener.VideoMetadataListener
import top.gochiusa.glplayer.listener.VideoSurfaceListener
import top.gochiusa.glplayer.mediacodec.CodecRendererFactory
import top.gochiusa.glplayer.util.Assert
import top.gochiusa.glplayer.util.Constants
import top.gochiusa.glplayer.util.PlayerLog
import java.io.IOException
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 播放器实现类
 */
class GLPlayer
private constructor(
    builder: Builder
) : Player, Handler.Callback {

    override val durationMs: Long
        get() = if (mediaSource.durationUs > 0) mediaSource.durationUs / 1000 else -1L
    override val currentPositionMs: Long
        get() = _currentPositionUs / 1000
    override val cacheDurationMs: Long
        get() = if (mediaSource.cacheDurationUs > 0) mediaSource.cacheDurationUs / 1000 else -1L

    @Volatile
    override var playAfterLoading: Boolean = builder.playAfterLoading

    private var _currentPositionUs: Long = 0L
    private var startRenderTimeMs: Long = -1L

    private val mayRenderFirstFrame: Boolean
    private val mediaSource: MediaSource
    private val renderers: Array<Renderer>
    private val requestHeaders: Map<String, String>?

    private val playbackThread: HandlerThread = HandlerThread("GLPlayer", Process.THREAD_PRIORITY_AUDIO)
    private val eventHandler: Handler

    private val applicationContext: Context = builder.context.applicationContext
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private var videoOutput: SurfaceView? = null
    private val componentListener: ComponentListener

    private var eventListenerSet: CopyOnWriteArraySet<EventListener> = CopyOnWriteArraySet()

    private var mediaItem: MediaItem? = null

    private var state: Int = Player.STATE_INIT

    init {
        playbackThread.start()
        eventHandler = Handler(playbackThread.looper, this)
        componentListener = ComponentListener()

        mayRenderFirstFrame = builder.renderFirstFrame
        mediaSource = builder.sourceFactory.createMediaSource(applicationContext)
        renderers = (builder.rendererFactory ?: CodecRendererFactory()).createRenders(
            eventHandler, componentListener)
        requestHeaders = builder.requestHeader
    }

    override fun play() {
        Assert.verifyMainThread()

        when(state)  {
             Player.STATE_STOP -> {
                 eventHandler.sendEmptyMessage(MSG_REPLAY)
            }
            Player.STATE_BUFFERING -> {
                // may delay DELAY_FOR_DECODE_TIME or old state is pause
                if (!eventHandler.hasMessages(MSG_PLAY)) {
                    eventHandler.sendEmptyMessageDelayed(MSG_PLAY,
                        DELAY_FOR_DECODE_TIME * 2)
                }
            }
            Player.STATE_PAUSE, Player.STATE_READY -> {
                eventHandler.sendEmptyMessage(MSG_PLAY)
            }
            else -> {}
        }
    }

    override fun pause() {
        Assert.verifyMainThread()
        if (state == Player.STATE_PLAYING) {
            eventHandler.sendEmptyMessage(MSG_PAUSE)
        }
    }

    override fun seekTo(positionMs: Long) {
        Assert.verifyMainThread()

        if (positionMs > durationMs || positionMs == currentPositionMs) {
            return
        }
        if (state == Player.STATE_PLAYING || state == Player.STATE_PAUSE
            || state == Player.STATE_STOP) {
            eventHandler.sendMessage(Message.obtain().apply {
                what = MSG_SEEK_TO
                obj = positionMs
            })
        }
    }

    override fun release() {
        Assert.verifyMainThread()

        if (state != Player.STATE_RELEASE) {
            state = Player.STATE_RELEASE
            eventHandler.removeCallbacksAndMessages(null)
            playbackThread.quit()
            renderers.forEach {
                it.disable()
                it.release()
            }
            mediaSource.release()
            videoOutput = null
            eventListenerSet.forEach {
                it.onPlaybackStateChanged(state)
            }
            eventListenerSet.clear()
        }
    }

    override fun setMediaItem(mediaItem: MediaItem) {
        this.mediaItem = mediaItem
    }

    override fun prepare() {
        if (mediaItem != null && state != Player.STATE_RELEASE) {
            eventHandler.sendMessage(Message.obtain().apply {
                what = MSG_PREPARE
                obj = mediaItem
            })
        }
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView) {
        Assert.verifyMainThread()

        if (state == Player.STATE_RELEASE) {
            return
        }

        this.videoOutput = surfaceView
        if (surfaceView is VideoMetadataListener) {
            componentListener.internalVideoMetadataListener = surfaceView
        }
        if (surfaceView is SurfaceProvider) {
            surfaceView.setOnVideoSurfaceListener(componentListener)
            setVideoOutputInternal(surfaceView.surface)
        } else {
            setVideoOutputInternal(surfaceView.holder.surface)
        }
    }

    override fun clearVideoSurfaceView(surfaceView: SurfaceView) {
        setVideoOutputInternal(null)

        this.videoOutput = null
        if (surfaceView is VideoMetadataListener) {
            componentListener.internalVideoMetadataListener = null
        }
        if (surfaceView is SurfaceProvider) {
            surfaceView.setOnVideoSurfaceListener(null)
        }
    }

    override fun addEventListener(eventListener: EventListener) {
        eventListenerSet.add(eventListener)
    }

    override fun removeEventListener(eventListener: EventListener) {
        eventListenerSet.remove(eventListener)
    }

    override fun isPlaying(): Boolean = state == Player.STATE_PLAYING

    override fun handleMessage(msg: Message): Boolean {
        return when (msg.what) {
            MSG_PREPARE -> {
                prepareInternal(msg.obj as MediaItem)
                true
            }
            MSG_RENDER -> {
                renderInternal(true)
                true
            }
            MSG_PLAY -> {
                playInternal()
                true
            }
            MSG_PAUSE -> {
                pauseInternal()
                true
            }
            MSG_SEEK_TO -> {
                seekToInternal(msg.obj as Long)
                true
            }
            MSG_REPLAY -> {
                replayInternal()
                true
            }
            else -> {
                false
            }
        }
    }

    private fun setVideoOutputInternal(videoOutput: Surface?) {
        if (videoOutput == null) {
            eventListenerSet.forEach { listener ->
                listener.onVideoSurfaceDetach()
            }
        } else {
            eventListenerSet.forEach { listener ->
                listener.onVideoSurfaceAttach()
            }
        }
        renderers.forEach {
            if (it is VideoSurfaceListener) {
                if (videoOutput != null) {
                    it.onVideoSurfaceCreated(videoOutput)
                } else {
                    it.onVideoSurfaceDestroyed(null)
                }
            }
        }
    }

    private fun prepareInternal(mediaItem: MediaItem) {
        eventHandler.removeMessages(MSG_RENDER)
        mainHandler.post {
            changeStateUncheck(Player.STATE_LOADING)
        }

        renderers.forEach {
            if (it.state == Renderer.STATE_ENABLE) {
                it.disable()
            }
        }
        // 重置播放位置
        _currentPositionUs = 0L

        runCatching {
            mediaSource.setDataSource(mediaItem, requestHeaders)
        }.onFailure {
            mainHandler.post {
                notifyError(SOURCE_ERROR)
                changeStateUncheck(Player.STATE_INIT)
            }
        }.onSuccess {
            val formats = mediaSource.format
            renderers.forEach { renderer ->
                renderer.enable(formats, mediaSource, 0L)
            }

            loopSendData()
            if (playAfterLoading) {
                eventHandler.sendEmptyMessageDelayed(MSG_PLAY, DELAY_FOR_DECODE_TIME)
            } else {
                if (mayRenderFirstFrame) {
                    delay(DELAY_FOR_DECODE_TIME)
                    renderVideoFrame(0L)
                }
                mainHandler.post {
                    changeStateUncheck(Player.STATE_READY)
                }
            }
        }
    }

    // call in main thread
    private fun changeStateUncheck(newValue: Int) {
        if (state != Player.STATE_RELEASE) {
            state = newValue
            eventListenerSet.forEach {
                it.onPlaybackStateChanged(newValue)
            }
        }
    }

    // call in main thread
    private fun notifyError(errorCode: Int) {
        if (state != Player.STATE_RELEASE) {
            eventListenerSet.forEach {
                it.onPlayerError(errorCode)
            }
        }
    }

    private fun renderInternal(loop: Boolean) {
        //val loopStart = SystemClock.elapsedRealtime()
        loopSendData()
        //PlayerLog.d(message = "loopSendData spend time ${SystemClock.elapsedRealtime() - loopStart}")

        val startTimeMs: Long = SystemClock.elapsedRealtime()
        renderers.forEach {
            try {
                it.render(_currentPositionUs, startTimeMs)
            } catch (e: IOException) {
                mainHandler.post {
                    notifyError(IO_ERROR)
                }
                PlayerLog.e(message = e)
            } catch (e: Exception) {
                PlayerLog.e(message = e)
            }
        }
        
        //val delayTimeMs = (5L - SystemClock.elapsedRealtime() + loopStart).coerceAtLeast(0)

        if (currentPositionMs >= durationMs) {
            mainHandler.post {
                changeStateUncheck(Player.STATE_STOP)
            }
            return
        }

        /*_currentPositionUs = ((SystemClock.elapsedRealtime() - startRenderTimeMs) * 1000
                + delayTimeMs).coerceAtMost(mediaSource.durationUs)*/
        _currentPositionUs = ((SystemClock.elapsedRealtime() - startRenderTimeMs) * 1000)
            .coerceAtMost(mediaSource.durationUs)

        //PlayerLog.d(message = "delay time $delayTimeMs")
        if (loop) {
            //eventHandler.sendEmptyMessageDelayed(MSG_RENDER, delayTimeMs)
            eventHandler.sendEmptyMessage(MSG_RENDER)
        }
    }

    private fun renderVideoFrame(positionUs: Long) {
        val startRenderTime = SystemClock.elapsedRealtime()
        renderers.forEach {
            if (it.getTrackType() == Constants.TRACK_TYPE_VIDEO) {
                runCatching {
                    it.render(positionUs, startRenderTime)
                }.onFailure { throwable ->
                    PlayerLog.d(message = throwable)
                }
            }
        }
    }

    private fun seekToInternal(positionMs: Long) {
        eventHandler.removeMessages(MSG_RENDER)
        val positionUs = positionMs * 1000

        val oldState = state
        mainHandler.post {
            changeStateUncheck(Player.STATE_BUFFERING)
        }
        renderers.forEach {
            it.onSeekTo()
        }
        val syncTime = mediaSource.seekTo(positionUs, SeekMode.CLOSEST_SYNC)
        //_currentPositionUs = if (syncTime < 0) positionUs else syncTime

        loopSendData()

        var delayTime = DELAY_FOR_DECODE_TIME
        if (oldState == Player.STATE_PAUSE) {
            delay(delayTime)
            delayTime = 0
            renderVideoFrame(syncTime)
        }
        // 更新播放位置
        _currentPositionUs = positionUs

        if (oldState != Player.STATE_STOP) {
            mainHandler.post {
                changeStateUncheck(oldState)
            }
        }
        when(oldState) {
            Player.STATE_PLAYING, Player.STATE_STOP -> {
                eventHandler.sendEmptyMessageDelayed(MSG_PLAY, delayTime)
            }
        }
    }

    private fun playInternal() {
        eventHandler.removeMessages(MSG_RENDER)
        eventHandler.removeMessages(MSG_PLAY)
        startRenderTimeMs = SystemClock.elapsedRealtime() - _currentPositionUs / 1000
        eventHandler.sendEmptyMessage(MSG_RENDER)
        mainHandler.post {
            changeStateUncheck(Player.STATE_PLAYING)
        }
    }

    private fun pauseInternal() {
        eventHandler.removeMessages(MSG_RENDER)
        mainHandler.post {
            changeStateUncheck(Player.STATE_PAUSE)
        }
    }

    private fun replayInternal() {
        renderers.forEach {
            it.onSeekTo()
        }
        val syncTime = mediaSource.seekTo(0L, SeekMode.CLOSEST_SYNC)
        _currentPositionUs = if (syncTime < 0) 0L else syncTime

        loopSendData()

        eventHandler.sendEmptyMessageDelayed(MSG_PLAY, DELAY_FOR_DECODE_TIME)
    }



    private fun loopSendData() {
        var sendData = true
        while (sendData) {
            try {
                sendData = mediaSource.sendData()
            } catch (e: IOException) {
                mainHandler.post {
                    notifyError(IO_ERROR)
                }
                throw e
            } catch (e: Exception) {
                PlayerLog.e(message = e)
            }
        }
    }

    /**
     * 尝试调用[Thread.sleep]来进行阻塞式等待
     */
    private fun delay(delayTimeMs: Long) {
        if (delayTimeMs > 0) {
            try {
                Thread.sleep(delayTimeMs)
            } catch (e: Exception) {
                PlayerLog.v(message = e)
            }
        }
    }


    class Builder(internal val context: Context) {
        internal var playAfterLoading: Boolean = false
        internal var renderFirstFrame: Boolean = false
        internal var rendererFactory: RendererFactory? = null
        internal var requestHeader: Map<String, String>? = null
        internal var sourceFactory: MediaSourceFactory = DefaultMediaSourceFactory()
        // TODO loop无限循环

        /**
         * 设置是否在媒体数据首次加载完成后自动播放，如果为true，则播放器将从[Player.STATE_LOADING]直接
         * 转入[Player.STATE_PLAYING]状态
         */
        fun setPlayAfterLoading(enable: Boolean): Builder {
            playAfterLoading = enable
            return this
        }

        /**
         * 设置如何构造[Renderer]的实现类
         */
        fun setRendererFactory(factory: RendererFactory): Builder {
            rendererFactory = factory
            return this
        }

        /**
         * 设置请求网络媒体资源的请求头（HTTP/HTTPS协议下）
         */
        fun setRequestHeader(headers: Map<String, String>): Builder {
            requestHeader = headers
            return this
        }

        /**
         * 设置如何构造[MediaSource]的实现类
         */
        fun setMediaSourceFactory(mediaSourceFactory: MediaSourceFactory): Builder {
            sourceFactory = mediaSourceFactory
            return this
        }

        /**
         * 设置是否渲染视频的首帧画面
         */
        fun setRenderFirstFrame(enable: Boolean): Builder {
            renderFirstFrame = enable
            return this
        }

        fun build(): Player {
            return GLPlayer(this)
        }
    }


    private inner class ComponentListener : VideoSurfaceListener, VideoMetadataListener {
        var internalVideoMetadataListener: VideoMetadataListener? = null
            set(value) {
                field = value
                if (::cacheFormat.isInitialized) {
                    value?.onVideoMetadataChanged(cacheFormat)
                }
            }
        lateinit var cacheFormat: Format

        override fun onVideoSurfaceCreated(surface: Surface) {
            setVideoOutputInternal(surface)
        }

        override fun onVideoSurfaceDestroyed(surface: Surface?) {
            setVideoOutputInternal(null)
        }

        override fun onVideoMetadataChanged(format: Format) {
            internalVideoMetadataListener?.onVideoMetadataChanged(format)

            cacheFormat = format
        }

    }

    companion object {
        private const val MSG_PREPARE = 1
        private const val MSG_RENDER = 2
        private const val MSG_SEEK_TO = 3
        private const val MSG_PLAY = 4
        private const val MSG_PAUSE = 5
        private const val MSG_REPLAY = 6

        private const val DELAY_FOR_DECODE_TIME = 100L

        /**
         * 提供的媒体数据无法成功加载数据
         */
        const val SOURCE_ERROR = 11

        const val IO_ERROR = 12
    }
}