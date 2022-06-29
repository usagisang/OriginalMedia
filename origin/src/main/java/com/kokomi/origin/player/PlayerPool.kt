package com.kokomi.origin.player

import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import com.kokomi.origin.appContext
import top.gochiusa.glplayer.GLPlayerBuilder
import top.gochiusa.glplayer.PlayerView
import top.gochiusa.glplayer.base.Player
import top.gochiusa.glplayer.entity.MediaItem

@MainThread
class PlayerPool(
    private val size: Int
) {

    companion object {
        private const val TAG = "PlayerPool"
        private const val MAIN_PLAYER_KEY = -1
    }

    private val playerMap by lazy {
        val map = hashMapOf<Int, Player>()
        for (i in -1 until size - 1) {
            val player = GLPlayerBuilder(appContext)
                .setInfiniteLoop(true)
                .build()
            map[i] = player
        }
        map
    }

    private var nextBufferKey = 0

    private val listener by lazy { PlayerEventListener() }

    internal fun prepare(playerView: PlayerView, url: String) {
        Log.i(TAG, "prepare - $nextBufferKey")
        playerMap[nextBufferKey]?.run {
            playerView.setPlayer(this)
            setMediaItem(MediaItem.fromUrl(url))
            prepare()
        }
        nextBufferKey = (nextBufferKey + 1) % (size - 1)
    }

    private var autoPlay = true
    private var autoResume = true

    internal fun pausePool() {
        mainPlayer?.let { player ->
            // 记录自动播放状态
            autoPlay = listener.autoPlay
            // 禁止自动播放
            listener.autoPlay = false
            // 检查是否可以暂停，记录以恢复状态
            autoResume = player.canPause()
            // 若可以暂停，则暂停
            if (autoResume) player.pause()
            Log.i(TAG, "pause - playerState = ${player.playerState} autoResume = $autoResume")
        }
    }

    internal fun resumePool() {
        Log.i(TAG, "resume - autoResume = $autoResume")
        mainPlayer?.let { player ->
//             恢复状态，根据 autoResume 来恢复
            if (autoResume) {
                if (player.playerState == Player.STATE_LOADING
                    || player.playerState == Player.STATE_BUFFERING
                ) {
                    listener.autoPlay = autoPlay
                } else {
                    player.play()
                }
            }
        }
    }

    internal fun pause(player: Player?) {
        player ?: return
        // 暂停
        if (player.canPause()) player.pause()
    }

    internal infix fun exchange(player: Player?) {
        mainPlayer?.let { main ->
            Log.i(TAG, "exchange - main state = ${main.playerState}")
            if (listener.bindPlayer == null) return@let
            // 禁止自动播放
            listener.bindPlayer = null
            listener.autoPlay = false
            // 解绑
            main.removeEventListener(listener)
            // 暂停
            if (main.canPause()) main.pause()
        }
        player?.let { target ->
            var index: Int = Int.MIN_VALUE
            playerMap.map {
                if (it.value == target) {
                    index = it.key
                }
            }
            if (index == Int.MIN_VALUE) throw IllegalArgumentException()
            Log.i(TAG, "exchange - target index = $index")
            val main = playerMap.put(MAIN_PLAYER_KEY, target)
            playerMap[index] = main!!
            Log.i(TAG, "exchange - target state = ${target.playerState}")
            when (target.playerState) {
                Player.STATE_READY -> {
                    target.play()
                }
                Player.STATE_LOADING, Player.STATE_INIT -> {
                    listener.autoPlay = true
                }
                Player.STATE_PAUSE -> {
                    target.play()
                    target.seekTo(0L)
                }
                else -> {
                    player.play()
                    // 到这里必定可以调用 seekTo ，因此就不检查状态了
                    player.seekTo(0L)
                }
            }
            Log.e(TAG, "exchange: player = $target")
            listener.bindPlayer = target
            target.addEventListener(listener)
        }
    }

    private val mainPlayer: Player?
        get() = playerMap[MAIN_PLAYER_KEY]

}