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

    private val mayRenderFirstFrame: Boolean = builder.renderFirstFrame
    private val infiniteLoop: Boolean = builder.infiniteLoop
    private val mediaSource: MediaSource
    private val renderers: Array<Renderer>
    private val requestHeaders: Map<String, String>?

    /**
     * 工作线程
     */
    private val playbackThread: HandlerThread = HandlerThread("GLPlayer",
        Process.THREAD_PRIORITY_AUDIO)

    /**
     * 绑定工作线程的Handler
     */
    private val eventHandler: Handler

    private val applicationContext: Context = builder.context.applicationContext
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private var videoOutput: SurfaceView? = null
    private val componentListener: ComponentListener

    private var eventListenerSet: CopyOnWriteArraySet<EventListener> = CopyOnWriteArraySet()

    private var mediaItem: MediaItem? = null

    /**
     * 播放器当前状态
     * 规定只能在主线程更改播放器状态并通知相应的监听器
     */
    @Volatile
    private var state: Int = Player.STATE_INIT

    /**
     * 播放器的上一个状态，目前只用于seekTo的状态保存
     */
    private var lastState: Int = Player.STATE_INIT

    init {
        playbackThread.start()
        eventHandler = Handler(playbackThread.looper, this)
        componentListener = ComponentListener()

        mediaSource = builder.sourceFactory.createMediaSource(applicationContext)
        renderers = (builder.rendererFactory ?: CodecRendererFactory()).createRenders(
            eventHandler, componentListener)
        requestHeaders = builder.requestHeader
    }

    override fun play() {
        Assert.verifyMainThread()

        when(state)  {
             Player.STATE_STOP -> {
                 eventHandler.sendMessage(Message.obtain().apply {
                     what = MSG_SEEK_TO
                     obj = 0L
                 })
            }
            Player.STATE_BUFFERING -> {
                // may delay DELAY_FOR_DECODE_TIME or old state is pause
                if (!eventHandler.hasMessages(MSG_PLAY)) {
                    eventHandler.sendEmptyMessageDelayed(MSG_PLAY,
                        DELAY_FOR_DECODE_MS * 2)
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
        if (state == Player.STATE_PLAYING || state == Player.STATE_BUFFERING) {
            eventHandler.sendEmptyMessage(MSG_PAUSE)
        }
    }

    override fun seekTo(positionMs: Long) {
        Assert.verifyMainThread()

        if (positionMs > durationMs || positionMs == currentPositionMs) {
            return
        }
        if (state == Player.STATE_PLAYING || state == Player.STATE_PAUSE
            || state == Player.STATE_STOP || state == Player.STATE_BUFFERING) {
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
            MSG_WAIT_FOR_CACHE -> {
                waitForCacheInternal(
                    msg.obj?.let {
                        it as Long
                    } ?: WAIT_TIME_AS_START_MS
                )
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
                eventHandler.sendEmptyMessageDelayed(MSG_PLAY, DELAY_FOR_DECODE_MS)
            } else {
                if (mayRenderFirstFrame) {
                    delay(DELAY_FOR_DECODE_MS)
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
        val loopStart = SystemClock.elapsedRealtime()
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

        // 如果已经到达末尾
        if (currentPositionMs >= durationMs) {
            if (infiniteLoop) {
                eventHandler.sendMessage(Message.obtain().apply {
                    what = MSG_SEEK_TO
                    obj = 0L
                })
            } else {
                mainHandler.post {
                    changeStateUncheck(Player.STATE_STOP)
                }
            }
            return
        }

        val delayTimeMs = (5L - SystemClock.elapsedRealtime() + loopStart).coerceAtLeast(1)
        _currentPositionUs = ((SystemClock.elapsedRealtime() - startRenderTimeMs) * 1000
                + delayTimeMs).coerceAtMost(mediaSource.durationUs)
        /*_currentPositionUs = ((SystemClock.elapsedRealtime() - startRenderTimeMs) * 1000)
            .coerceAtMost(mediaSource.durationUs)*/

        // 如果数据源缓存不足
        if (shouldWaitForCache()) {
            eventHandler.sendMessage(Message.obtain().apply {
                what = MSG_WAIT_FOR_CACHE
                obj = WAIT_TIME_AS_START_MS
            })
            mainHandler.post {
                changeStateUncheck(Player.STATE_BUFFERING)
            }
            return
        }

        //PlayerLog.d(message = "delay time $delayTimeMs")
        if (loop) {
            eventHandler.sendEmptyMessageDelayed(MSG_RENDER, delayTimeMs)
            //eventHandler.sendEmptyMessage(MSG_RENDER)
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
        // 如果存在较新的seekTo操作，取消本次操作
        if (eventHandler.hasMessages(MSG_SEEK_TO)) {
            return
        }
        // 取消缓存等待
        eventHandler.removeMessages(MSG_WAIT_FOR_CACHE)
        eventHandler.removeMessages(MSG_RENDER)
        val positionUs = positionMs * 1000

        // 缓存Buffing状态之前的状态，结束seekTo操作后需要自动恢复该状态
        var oldState = state
        if (oldState != Player.STATE_BUFFERING) {
            mainHandler.post {
                changeStateUncheck(Player.STATE_BUFFERING)
            }
        }

        renderers.forEach {
            it.onSeekTo()
        }
        val syncTime = mediaSource.seekTo(positionUs, SeekMode.CLOSEST_SYNC)
        //_currentPositionUs = if (syncTime < 0) positionUs else syncTime

        loopSendData()
        // 更新播放位置
        _currentPositionUs = positionUs

        // 存在多个seekTo操作，延迟状态的自动恢复
        if (eventHandler.hasMessages(MSG_SEEK_TO)) {
            // Buffing状态无需缓存
            if (oldState != Player.STATE_BUFFERING) {
                lastState = oldState
                // 延迟处理oldState
                return
            }
        } else {
            if (oldState == Player.STATE_BUFFERING) {
                oldState = lastState
            }
        }

        when(oldState) {
            Player.STATE_PLAYING, Player.STATE_STOP -> {
                // 缓存告急，不应当退出Buffing状态，而应当继续等待
                if (shouldWaitForCache()) {
                    eventHandler.sendMessage(Message.obtain().apply {
                        what = MSG_WAIT_FOR_CACHE
                        obj = WAIT_TIME_AS_START_MS
                    })
                } else {
                    eventHandler.sendEmptyMessageDelayed(MSG_PLAY, DELAY_FOR_DECODE_MS)
                }
            }
            Player.STATE_PAUSE -> {
                delay(DELAY_FOR_DECODE_MS)
                renderVideoFrame(syncTime)
                // 重新进入Pause状态
                mainHandler.post {
                    changeStateUncheck(Player.STATE_PAUSE)
                }
            }
        }
    }

    private fun playInternal() {
        eventHandler.removeMessages(MSG_RENDER)
        // 避免重复进行Play操作
        eventHandler.removeMessages(MSG_PLAY)
        // 取消缓存等待
        eventHandler.removeMessages(MSG_WAIT_FOR_CACHE)
        // 更新起始时间
        startRenderTimeMs = SystemClock.elapsedRealtime() - _currentPositionUs / 1000
        eventHandler.sendEmptyMessage(MSG_RENDER)
        mainHandler.post {
            changeStateUncheck(Player.STATE_PLAYING)
        }
    }

    private fun pauseInternal() {
        // 检查是否已经自动恢复到Pause状态(seekTo中)
        if (state != Player.STATE_PAUSE) {
            eventHandler.removeMessages(MSG_RENDER)
            eventHandler.removeMessages(MSG_PLAY)
            eventHandler.removeMessages(MSG_WAIT_FOR_CACHE)
            mainHandler.post {
                changeStateUncheck(Player.STATE_PAUSE)
            }
            renderers.forEach {
                it.onPause()
            }
        }
    }

    private fun waitForCacheInternal(delayTimeMs: Long) {
        eventHandler.removeMessages(MSG_RENDER)
        eventHandler.removeMessages(MSG_WAIT_FOR_CACHE)

        if (shouldStopWait()) {
            eventHandler.sendEmptyMessage(MSG_PLAY)
        } else {
            eventHandler.sendMessageDelayed(
                Message.obtain().apply {
                    what = MSG_WAIT_FOR_CACHE
                    obj = (delayTimeMs * 2).coerceAtMost(MAX_WAIT_TIME_MS)
                }, delayTimeMs
            )
        }
    }

    private fun loopSendData() {
        try {
            var sendData = true
            while (sendData) {
                sendData = mediaSource.sendData()
            }
        } catch (e: IOException) {
            mainHandler.post {
                notifyError(IO_ERROR)
            }
            PlayerLog.e(message = e)
        } catch (e: Exception) {
            PlayerLog.e(message = e)
        }
    }

    /**
     * 是否应该进行缓存等待
     */
    private fun shouldWaitForCache(): Boolean = !mediaSource.hasCacheReachedEndOfStream() &&
            mediaSource.cacheDurationUs in 0L until MIN_CACHE_DURATION_US

    /**
     * 是否应该停止缓存等待
     */
    private fun shouldStopWait(): Boolean = mediaSource.hasCacheReachedEndOfStream() ||
            mediaSource.cacheDurationUs >= MAX_CACHE_DURATION_US

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
        internal var infiniteLoop: Boolean = false

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

        /**
         * 是否允许播放器无限循环播放媒体
         */
        fun setInfiniteLoop(enable: Boolean): Builder {
            infiniteLoop = enable
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
        private const val MSG_WAIT_FOR_CACHE = 6

        private const val DELAY_FOR_DECODE_MS = 100L

        /**
         * 如果缓存时长低于此值，则应进入等待状态
         */
        private const val MIN_CACHE_DURATION_US = 1000000L

        /**
         * 如果缓存时长高于此值，则应离开等待状态
         */
        private const val MAX_CACHE_DURATION_US = 3500000L

        /**
         * Cache轮询初始时间
         */
        private const val WAIT_TIME_AS_START_MS = 100L

        /**
         * Cache轮询的最大时间
         */
        private const val MAX_WAIT_TIME_MS = 800L

        /**
         * 提供的媒体数据无法成功加载数据
         */
        const val SOURCE_ERROR = 11

        const val IO_ERROR = 12
    }
}