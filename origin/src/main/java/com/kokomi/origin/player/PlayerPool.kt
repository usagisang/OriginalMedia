package com.kokomi.origin.player

import androidx.annotation.MainThread
import com.kokomi.origin.appContext
import top.gochiusa.glplayer.GLPlayerBuilder
import top.gochiusa.glplayer.PlayerView
import top.gochiusa.glplayer.base.Player
import top.gochiusa.glplayer.entity.MediaItem

@MainThread
internal class PlayerPool(
    private val playerPoolSize: Int = 5
) {

    private val playerMainIndex = playerPoolSize - 1

    private var bufferPlayerQueueHead = 0

    private val eventListener by lazy { PlayerEventListener() }

    /**
     * 最后一个是主播放器(MainPlayer)
     * <p>
     *
     * 其它都是缓冲播放器(BufferPlayer)
     * <p>
     *
     * 只有主播放器可以播放，缓冲播放器只可以缓冲
     * <p>
     *
     * 主播放器权是可以交换的，但是必须在主线程内交换
     * @see PlayerPool.exchange
     * <p>
     * */
    private val players by lazy {
        mutableListOf<Player>().apply {
            for (i in 1..playerPoolSize) {
                add(
                    GLPlayerBuilder(appContext)
                        .setInfiniteLoop(true)
                        .setRenderFirstFrame(true)
                        .build()
                )
            }
        }
    }

    internal fun prepare(playerView: PlayerView, url: String) {
        players[bufferPlayerQueueHead].run {
            playerView.setPlayer(this)
            setMediaItem(MediaItem.fromUrl(url))
            prepare()
        }
        bufferPlayerQueueHead = (bufferPlayerQueueHead + 1) % (playerPoolSize - 1)
    }

    internal fun pauseIfNecessary(targetPlayer: Player) {
        targetPlayer.removeEventListener(eventListener)
        if (targetPlayer.canPause(targetPlayer.playerState)) targetPlayer.pause()
        eventListener.bindPlayer = null
    }

    /**
     * 交换主播放器权
     * */
    internal infix fun exchange(targetPlayer: Player) {
        val index = players.indexOf(targetPlayer)
        if (index == -1) {
            throw IllegalStateException("The Player does not belong to this pool.")
        }

        val mainPlayer = mainPlayer

        // 交换主播放器权
        players[playerMainIndex] = targetPlayer
        players[index] = mainPlayer

        // 开始新主播放器的播放，旧的监听器由旧的主播放器自行解除
        eventListener.bindPlayer = targetPlayer
        eventListener.autoPlay = true
        eventListener.autoSeekToStart = true
        targetPlayer.addEventListener(eventListener)
    }

    /**
     * 恢复绑定播放器的事件监听器
     * */
    internal fun resumePool() {
        val player = mainPlayer
        // 重新绑定
        eventListener.bindPlayer = player
        player.addEventListener(eventListener)
    }

    /**
     * 解绑事件监听器并暂停播放
     *
     * @return 若主播放器此时正在播放，返回 true ，否则返回 false
     * */
    internal fun pausePool(): Boolean {
        val player = mainPlayer
        // 解绑
        player.removeEventListener(eventListener)
        eventListener.bindPlayer = null
        eventListener.autoSeekToStart = false
        // 若可以暂停，则暂停
        if (player.canPause(player.playerState)) {
            player.pause()
        }
        eventListener.autoPlay = player.playerState != STATE_PAUSE
        return player.isPlaying()
    }

    private val mainPlayer: Player
        get() = players[playerMainIndex]

}