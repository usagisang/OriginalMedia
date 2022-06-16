package top.gochiusa.glplayer.base

import android.view.SurfaceView
import top.gochiusa.glplayer.entity.MediaItem
import top.gochiusa.glplayer.listener.EventListener

/**
 * 播放器接口。注意，大多数的状态转换需要满足条件
 */
interface Player {

    /**
     * 播放器状态转为[Player.STATE_PLAYING]，并继续/开始媒体播放
     */
    fun play()

    /**
     * 播放器状态转为[Player.STATE_PAUSE]，并暂停媒体播放
     */
    fun pause()

    /**
     * 将播放进度设置为指定位置，播放器状态将转为[Player.STATE_BUFFERING]
     * 直到seekTo动作完成，播放器将自动回到原状态
     */
    fun seekTo(positionMs: Long)

    /**
     * 在主线程中的任意时刻可安全调用，释放播放器资源，播放器状态将转为[Player.STATE_RELEASE]
     * 此播放器后续不能再次用于视频播放
     */
    fun release()

    /**
     * 设置需要加载的媒体
     */
    fun setMediaItem(mediaItem: MediaItem)

    /**
     * 开始加载媒体数据，播放器状态转为[Player.STATE_LOADING]
     * 如果[playAfterLoading]属性为true，播放器的状态将在准备完毕之后转为[Player.STATE_PLAYING]，开始媒体播放；
     * 否则，播放器的状态将在准备完毕之后转为[Player.STATE_READY]，等待[play]调用后开始媒体播放
     */
    fun prepare()

    /**
     * 将播放器与具有渲染能力的View相关联。目前View用于提供视频渲染能力，音频渲染不受影响
     */
    fun setVideoSurfaceView(surfaceView: SurfaceView)

    /**
     * 清除播放器与具有渲染能力的View的关联
     */
    fun clearVideoSurfaceView(surfaceView: SurfaceView)

    /**
     * 添加与播放器相关的事件监听器
     */
    fun addEventListener(eventListener: EventListener)

    /**
     * 移除相关的事件监听器
     */
    fun removeEventListener(eventListener: EventListener)

    /**
     * 播放器处于[Player.STATE_PLAYING]状态时为true，其他状态均为false
     */
    fun isPlaying(): Boolean

    /**
     * 媒体的总时长，单位为毫秒，如果尚未加载媒体信息，返回负值
     */
    val durationMs: Long

    /**
     * 当前播放进度，单位毫秒，尚未开始播放时，返回0
     */
    val currentPositionMs: Long

    /**
     * 已缓存的媒体长度，单位毫秒，如果尚未加载媒体信息，返回负值
     */
    val cacheDurationMs: Long

    /**
     * 如果为true，允许媒体数据初次加载完成后自动播放
     */
    var playAfterLoading: Boolean

    companion object {
        const val STATE_INIT = 0
        const val STATE_LOADING = 1
        const val STATE_READY = 2
        const val STATE_PLAYING = 3
        const val STATE_PAUSE = 4
        const val STATE_BUFFERING = 5
        const val STATE_STOP = 6
        const val STATE_RELEASE = 7
    }
}