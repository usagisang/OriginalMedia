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
    private lateinit var mediaSource: MediaSource
    private val mediaSourceFactory: MediaSourceFactory
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
        mediaSourceFactory = builder.sourceFactory
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

        if (positionMs > durationMs) {
            return
        }
        if (state == Player.STATE_PLAYING || state == Player.STATE_PAUSE) {
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
            mediaSource.release()
            renderers.forEach {
                it.disable()
                it.release()
            }
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
                eventHandler.removeMessages(MSG_PLAY)
                startRenderTimeMs = SystemClock.elapsedRealtime() - _currentPositionUs / 1000
                eventHandler.sendEmptyMessage(MSG_RENDER)
                mainHandler.post {
                    changeStateUncheck(Player.STATE_PLAYING)
                }
                true
            }
            MSG_PAUSE -> {
                eventHandler.removeMessages(MSG_RENDER)
                mainHandler.post {
                    changeStateUncheck(Player.STATE_PAUSE)
                }
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
        if (this::mediaSource.isInitialized) {
            mediaSource.release()
        }
        mediaSource = mediaSourceFactory.createMediaSource(applicationContext)
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
                    mainHandler.post {
                        changeStateUncheck(Player.STATE_READY)
                    }
                } else {
                    mainHandler.post {
                        changeStateUncheck(Player.STATE_READY)
                    }
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
        loopSendData()

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
        // 计算渲染所使用的时间
        val spendTimeMs = (SystemClock.elapsedRealtime() - startTimeMs)

        // TODO 音频首次渲染不稳定
        //val delayTimeMs = (5L - spendTimeMs).coerceAtLeast(0)

        // PlayerLog.d(message = "spend time $spendTimeMs")
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
            // eventHandler.sendEmptyMessageDelayed(MSG_RENDER, delayTimeMs)
            eventHandler.sendEmptyMessage(MSG_RENDER)
        }
    }

    private fun renderVideoFrame(positionUs: Long) {
        // TODO 渲染可能失败(pending)
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
        _currentPositionUs = if (syncTime < 0) positionUs else syncTime

        loopSendData()

        var delayTime = DELAY_FOR_DECODE_TIME
        if (oldState == Player.STATE_PAUSE) {
            delay(delayTime)
            delayTime -= delayTime
            renderVideoFrame(_currentPositionUs)
        }

        mainHandler.post {
            changeStateUncheck(oldState)
        }
        if (oldState == Player.STATE_PLAYING) {
            eventHandler.sendEmptyMessageDelayed(MSG_PLAY, delayTime)
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

        /**
         * 设置是否在媒体数据首次加载完成后自动播放
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

        override fun onVideoSurfaceCreated(surface: Surface) {
            setVideoOutputInternal(surface)
        }

        override fun onVideoSurfaceDestroyed(surface: Surface?) {
            setVideoOutputInternal(null)
        }

        override fun onVideoMetadataChanged(format: Format) {
            internalVideoMetadataListener?.onVideoMetadataChanged(format)
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