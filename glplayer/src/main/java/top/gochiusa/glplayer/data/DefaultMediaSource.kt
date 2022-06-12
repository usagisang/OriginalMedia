package top.gochiusa.glplayer.data

import android.content.Context
import android.media.MediaExtractor
import top.gochiusa.glplayer.base.Receiver
import top.gochiusa.glplayer.base.Sample
import top.gochiusa.glplayer.entity.Format
import top.gochiusa.glplayer.entity.MediaItem
import top.gochiusa.glplayer.entity.SampleData
import top.gochiusa.glplayer.util.PlayerLog
import java.io.IOException
import java.nio.ByteBuffer

class DefaultMediaSource(
    private var context: Context?
) : MediaSource {

    // TODO 视频断流与恢复
    // TODO 视频缓冲
    private val mediaExtractor: MediaExtractor = MediaExtractor()
    //private val audioTimeQueue: Deque<Long> = ArrayDeque()

    private val _formats: MutableList<Format> = mutableListOf()
    override val format: List<Format> = _formats

    private val receiverMap: MutableMap<Int, Receiver> = mutableMapOf()

    private var hasNext = true
    private val sample: Sample by lazy { DefaultSample() }


    override fun bindTrack(format: Format, receiver: Receiver) {
        mediaExtractor.selectTrack(format.trackIndex)
        receiverMap[format.trackIndex] = receiver
    }

    override fun sendData(): Boolean {
        if (mediaExtractor.sampleTrackIndex < 0) {
            return if (hasNext) {
                hasNext = mediaExtractor.advance()
                true
            } else {
                receiverMap.forEach {
                    it.value.receiveData(sample)
                }
                false
            }
        }
        val receiver = receiverMap[mediaExtractor.sampleTrackIndex]
        if (receiver == null) {
            hasNext = mediaExtractor.advance()
            return true
        }
        val consumed = receiver.receiveData(sample)
        if (consumed) {
            hasNext = mediaExtractor.advance()
        }
        return consumed
    }

    override fun unbindTrack(format: Format, receiver: Receiver) {
        mediaExtractor.unselectTrack(format.trackIndex)
        receiverMap.remove(format.trackIndex)
    }

    private var _durationUs: Long = -1L
    override val durationUs: Long
        get() = _durationUs

    override val cacheDurationUs: Long
        get() = mediaExtractor.cachedDuration

    private lateinit var mediaItem: MediaItem

    override fun setDataSource(
        mediaItem: MediaItem,
        requestHeaders: Map<String, String>?
    ) {
        this.mediaItem = mediaItem
        //audioTimeQueue.clear()

        // may block
        mediaItem.uri?.let {
            val context = context ?: throw IOException("cannot open the uri")
            mediaExtractor.setDataSource(context, it, requestHeaders)
        } ?: let {
            mediaExtractor.setDataSource(mediaItem.url)
        }
        _formats.clear()
        parseFormats()
    }

    private fun getNextScheduleUs(): Long {
        /*val next = audioTimeQueue.pollFirst()
            ?: if (hasNext) {
                -1L
            } else {
                _durationUs
            }
        return next*/
        return -1L
    }

    override fun release() {
        mediaExtractor.release()
    }

    override fun seekTo(positionUs: Long, seekMode: SeekMode): Long {
        //audioTimeQueue.clear()
        mediaExtractor.seekTo(positionUs, seekMode.mode)
        hasNext = true
        var cacheUs = mediaExtractor.cachedDuration
        var count = 0
        while (cacheUs < 500000 && count < 10) {
            try {
                Thread.sleep(10)
            } catch (e: Exception) {
                PlayerLog.v(message = e)
            }
            count ++
            cacheUs = mediaExtractor.cachedDuration
        }
        return mediaExtractor.sampleTime
    }

    private fun parseFormats() {
        for (i in 0 until mediaExtractor.trackCount) {
            val format = Format(mediaExtractor.getTrackFormat(i), i)
            _formats.add(format)
            if (_durationUs < 0) {
                _durationUs = format.duration
            }
        }
    }

    inner class DefaultSample: Sample {

        override fun readData(format: Format, byteBuffer: ByteBuffer): SampleData {
            if (!hasNext) {
                return SampleData(-1, _durationUs, 0, true)
            }

            val sampleTime = mediaExtractor.sampleTime
            val sampleFlags = mediaExtractor.sampleFlags

            val sampleSize = mediaExtractor.readSampleData(byteBuffer, 0)

            /*if (format.isAudio()) {
                audioTimeQueue.offerLast(sampleTime)
            }*/
            // TODO 根据情况来确定是否到达end
            return SampleData(sampleSize, sampleTime, sampleFlags, false)
        }

    }
}

class DefaultMediaSourceFactory: MediaSourceFactory {
    override fun createMediaSource(context: Context): MediaSource {
        return DefaultMediaSource(context)
    }
}