package com.kokomi.origin.player

import androidx.annotation.MainThread
import com.kokomi.origin.appContext
import top.gochiusa.glplayer.GLPlayer
import top.gochiusa.glplayer.PlayerView
import top.gochiusa.glplayer.base.Player
import top.gochiusa.glplayer.entity.MediaItem

@MainThread
internal class PlayerPool(
    private val playerPoolSize: Int = 5
) {

    private val playerMainIndex = playerPoolSize - 1

    private var bufferPlayerQueueHead = 0

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
     * @see PlayerPool.exchangeMainPlayer
     * <p>
     * */
    private val players by lazy {
        mutableListOf<Player>().apply {
            for (i in 1..playerPoolSize) {
                add(
                    GLPlayer.Builder(appContext)
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

    /**
     * 交换主播放器权
     * */
    internal infix fun exchangeMainPlayer(targetPlayer: Player) {
        val index = players.indexOf(targetPlayer)
        if (index == -1) {
            throw IllegalStateException("The Player does not belong to this pool.")
        }
        // 若传入的播放器就是主播放器
        if (index == playerMainIndex) {
            targetPlayer.play()
            return
        }
        val mainPlayer = players[playerMainIndex]
        // 暂停主播放器的播放
        mainPlayer.playAfterLoading = false
        if (mainPlayer.isPlaying()) {
            mainPlayer.pause()
        }
        // 交换主播放器权
        players[playerMainIndex] = targetPlayer
        players[index] = mainPlayer
        // 开始新主播放器的播放
        targetPlayer.playAfterLoading = true
        if (!targetPlayer.isPlaying()) {
            targetPlayer.seekTo(0)
            targetPlayer.play()
        }
    }

    internal fun mainPlayerPlay() {
        players[playerMainIndex].play()
    }

    internal fun mainPlayerPause() {
        players[playerMainIndex].pause()
    }

}