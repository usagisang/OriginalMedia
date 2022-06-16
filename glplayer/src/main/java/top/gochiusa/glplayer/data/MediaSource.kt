package top.gochiusa.glplayer.data

import top.gochiusa.glplayer.base.Sender
import top.gochiusa.glplayer.entity.Format
import top.gochiusa.glplayer.entity.MediaItem

interface MediaSource: Sender {

    fun setDataSource(mediaItem: MediaItem, requestHeaders: Map<String, String>?)

    fun release()

    fun seekTo(positionUs: Long, seekMode: SeekMode): Long

    val durationUs: Long
    val cacheDurationUs: Long
    val format: List<Format>
}