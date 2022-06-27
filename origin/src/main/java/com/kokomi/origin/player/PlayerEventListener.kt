package com.kokomi.origin.player

import top.gochiusa.glplayer.base.Player
import top.gochiusa.glplayer.listener.EventListenerAdapter

class PlayerEventListener : EventListenerAdapter {

    internal var bindPlayer: Player? = null

    internal var autoPlay: Boolean = true

    internal var autoSeekToStart: Boolean = false

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            STATE_READY -> {
                if (autoPlay) {
                    // 消耗自动播放事件
                    autoPlay = false
                    bindPlayer?.play()
                }
            }
            STATE_PAUSE -> {
                if (autoSeekToStart) {
                    autoSeekToStart = false
                    bindPlayer?.seekTo(0L)
                } else if (autoPlay) {
                    // 消耗自动播放事件
                    autoPlay = false
                    bindPlayer?.play()
                }
            }
        }
    }

}

/**
 * 播放器的初始状态，如果准备媒体资源失败(in [Player.STATE_LOADING])，则会回退到该状态
 */
const val STATE_INIT = 0

/**
 * 初次加载媒体资源，解析MetaData并缓冲媒体流，如果设置了[playAfterLoading]，则准备完毕后
 * 转入[Player.STATE_PLAYING]，否则，转入[Player.STATE_READY]
 *
 * 此状态可以收到调用[pause]的事件，在准备完毕后状态转入[Player.STATE_PAUSE]，此时[playAfterLoading]
 * 和[top.gochiusa.glplayer.GLPlayer.Builder.setRenderFirstFrame]失效
 */
const val STATE_LOADING = 1

/**
 * 媒体资源准备完毕而等待播放指令的状态，可以调用[play]转入[Player.STATE_PLAYING]
 */
const val STATE_READY = 2

/**
 * 播放器处于播放状态，此状态下会持续渲染媒体流
 *
 * 在播放结束后如果未设置[top.gochiusa.glplayer.GLPlayer.Builder.setInfiniteLoop]，
 * 则会转入[Player.STATE_STOP]
 */
const val STATE_PLAYING = 3

/**
 * 媒体渲染处于暂停状态
 *
 * 可以通过[play]来恢复播放状态
 *
 * [seekTo]在此状态下可以生效，但不会自动解除暂停状态
 */
const val STATE_PAUSE = 4

/**
 * 媒体源缓冲区数据不足，需要进行等待
 */
const val STATE_BUFFERING = 5

/**
 * 媒体播放结束，此状态目前只能由[Player.STATE_PLAYING]自然转入。可以调用[play]或[seekTo]重新进入播放状态
 */
const val STATE_STOP = 6

/**
 * 播放器终态，此状态下释放所有资源，并不能再用于媒体渲染
 */
const val STATE_RELEASE = 7