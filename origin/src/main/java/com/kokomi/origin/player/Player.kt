package com.kokomi.origin.player

import android.content.Context
import com.kokomi.origin.appContext
import top.gochiusa.glplayer.GLPlayer
import top.gochiusa.glplayer.base.Player
import top.gochiusa.glplayer.entity.MediaItem
import top.gochiusa.glplayer.listener.EventListener

object GLPlayer {

    internal fun play(url: String) {
        if (glPlayer.isPlaying()) glPlayer.pause()
        glPlayer.setMediaItem(MediaItem.fromUrl(url))
        glPlayer.prepare()
    }

}

internal val glPlayer: Player by lazy {
    GLPlayer.Builder(appContext)
        .setRenderFirstFrame(true)
        .setInfiniteLoop(true)
        .setPlayAfterLoading(true)
        .setRenderFirstFrame(true)
        .build().apply {
            addEventListener(object : EventListener {
                override fun onPlayerError(errorCode: Int) {
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    println(playbackState)
                }

                override fun onVideoSurfaceAttach() {
                }

                override fun onVideoSurfaceDetach() {
                }

            })
        }
}