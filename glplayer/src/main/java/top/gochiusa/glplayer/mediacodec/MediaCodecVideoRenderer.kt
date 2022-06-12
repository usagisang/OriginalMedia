package top.gochiusa.glplayer.mediacodec

import android.media.MediaCodec
import android.os.Build
import android.view.Surface
import top.gochiusa.glplayer.base.MediaClock
import top.gochiusa.glplayer.base.Sender
import top.gochiusa.glplayer.entity.Format
import top.gochiusa.glplayer.entity.MediaCodecConfiguration
import top.gochiusa.glplayer.listener.VideoMetadataListener
import top.gochiusa.glplayer.listener.VideoSurfaceListener
import top.gochiusa.glplayer.util.Constants
import top.gochiusa.glplayer.util.PlayerLog
import java.nio.ByteBuffer

class MediaCodecVideoRenderer(
    private val leadingLimitUs: Long = 1000000L,
    renderTimeLimitMs: Long = LIMIT_NOT_SET,
    private val syncLimitUs: Long = DEFAULT_VIDEO_SYNC_LIMIT,
    private var videoMetadataListener: VideoMetadataListener? = null
): MediaCodecRenderer(Constants.TRACK_TYPE_VIDEO, renderTimeLimitMs), VideoSurfaceListener {

    private val videoClock: MediaClock by lazy { VideoClock() }

    private var surface: Surface? = null
    private var videoFormat: Format? = null

    private var frameLossCount = 0

    override fun onSenderChanged(
        format: List<Format>,
        sender: Sender?,
        startPositionUs: Long
    ) {
        super.onSenderChanged(format, sender, startPositionUs)
        frameLossCount = 0
        if (videoFormat != null) {
            releaseCodec()
            videoFormat = null
        }
        for (f in sampleFormats) {
            if (f.isVideo()) {
                videoFormat = f
                sender?.bindTrack(f, this)
                videoMetadataListener?.onVideoMetadataChanged(f)
                return
            }
        }
        if (sampleFormats.isNotEmpty()) {
            PlayerLog.w(
                message = "The media file does not contain media types supported by " +
                        "MediaCodecVideoRenderer"
            )
        }
    }

    override fun processOutputBuffer(
        positionUs: Long,
        elapsedRealtimeUs: Long,
        codec: MediaCodec,
        buffer: ByteBuffer?,
        bufferIndex: Int,
        bufferFlags: Int,
        bufferSize: Int,
        bufferPresentationTimeUs: Long
    ): Boolean {
        if (surface == null || bufferSize <= 0) {
            buffer?.clear()
            codec.releaseOutputBuffer(bufferIndex, false)
            return true
        }
        val syncLimit = if (syncLimitUs > 0) syncLimitUs else 1

        val syncRange = (bufferPresentationTimeUs - syncLimit)..(bufferPresentationTimeUs
                + syncLimit)
        // 当前播放位置处于可同步的范围内(不超出此帧的PTS前后syncLimitMs内)
        return if (positionUs in syncRange) {
            try {
                codec.releaseOutputBuffer(bufferIndex, true)
                frameLossCount = maxOf(frameLossCount - 3, 0)
            } catch (error: MediaCodec.CodecException) {
                // 如果报出MediaCodec.CodecException，有可能是释放了一个不完整的帧
                PlayerLog.e(message = error)
            }
            true
        } else if (bufferPresentationTimeUs in positionUs..(positionUs + leadingLimitUs)) {
            //PlayerLog.d(message = "frame pending release")
            // 此帧超前，但不超过leadingLimitMs指定的最大限度
            false
        } else {
            //PlayerLog.d(message = "Frame loss. At Time $positionUs bufferTime $bufferPresentationTimeUs")
            frameLossCount++
            codec.releaseOutputBuffer(bufferIndex, false)
            true
        }
    }

    override fun getMediaCodecConfiguration(): MediaCodecConfiguration? {
        val format = videoFormat
        val s = surface
        if (s == null || format == null) {
            return null
        }
        return format.sampleMimeType?.let {
            MediaCodecConfiguration(
                type = it,
                format = format.mediaFormat,
                surface = s,
                crypto = null,
                flags = 0
            )
        }
    }

    override fun getFormat(): Format? = videoFormat

    override fun getMediaClock(): MediaClock {
        return videoClock
    }

    override fun onVideoSurfaceCreated(surface: Surface) {
        setOutput(surface)
    }

    override fun onVideoSurfaceDestroyed(surface: Surface?) {
        setOutput(null)
    }


    private fun setOutput(output: Surface?) {
        if (output == surface) {
            return
        }
        val mediaCodec = codec
        // 如果mediaCodec已经创建并且存在旧的输出平面、并且新的平面不为空、并且API版本大于等于23
        if (mediaCodec != null && surface != null && output != null &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaCodec.setOutputSurface(output)
        } else {
            releaseCodec()
        }
        surface = output
    }

    inner class VideoClock: MediaClock {
        override fun getPositionUs(): Long = lastPositionUs
    }
}