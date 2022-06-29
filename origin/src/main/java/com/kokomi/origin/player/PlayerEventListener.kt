package com.kokomi.origin.player

import android.util.Log
import top.gochiusa.glplayer.base.Player
import top.gochiusa.glplayer.listener.EventListenerAdapter

class PlayerEventListener : EventListenerAdapter {

    companion object {
        private const val TAG = "PlayerListener"
    }

    internal var bindPlayer: Player? = null

    internal var autoPlay: Boolean = true

    override fun onPlaybackStateChanged(playbackState: Int) {
        Log.i(TAG, "onPlaybackStateChanged: $bindPlayer")
        Log.i(TAG, "stateChanged: $autoPlay")
        Log.i(TAG, "stateChanged: $playbackState")
        when (playbackState) {
            Player.STATE_READY -> {
                if (autoPlay) {
                    // 消耗自动播放事件
                    autoPlay = false
                    bindPlayer?.play()
                } else {
                    bindPlayer?.pause()
                }
            }
            Player.STATE_LOADING, Player.STATE_BUFFERING -> {

            }
        }
    }

}