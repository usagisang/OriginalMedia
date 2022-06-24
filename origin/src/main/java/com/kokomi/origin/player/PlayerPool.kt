package com.kokomi.origin.player

import android.util.Log
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

    private val players by lazy {
        mutableListOf<Player>().apply {
            // 最后一个是主播放器，只有主播放器可以播放，其它只可以缓冲
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
//        Log.e("TAG", "prepare: $bufferPlayerQueueHead")
        players[bufferPlayerQueueHead].run {
            playerView.setPlayer(this)
            setMediaItem(MediaItem.fromUrl(url))
            prepare()
        }
        bufferPlayerQueueHead = (bufferPlayerQueueHead + 1) % (playerPoolSize - 1)
    }

    internal fun exchangeMainPlayer(player: Player) {
        val index = players.indexOf(player)
        if (index == playerMainIndex || index == -1) {
            player.play()
            return
        }
        val main = players[playerMainIndex]
        main.playAfterLoading = false
        players[playerMainIndex] = player
        players[index] = main
        player.playAfterLoading = true
        if(!player.isPlaying()) player.play()
    }

    internal fun mainPlayerPlay() {
        players[playerMainIndex].play()
    }

    internal fun mainPlayerPause() {
        players[playerMainIndex].pause()
    }

}