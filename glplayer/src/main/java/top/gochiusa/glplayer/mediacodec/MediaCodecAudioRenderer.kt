package top.gochiusa.glplayer.mediacodec

import android.media.*
import top.gochiusa.glplayer.base.MediaClock
import top.gochiusa.glplayer.base.Sender
import top.gochiusa.glplayer.entity.Format
import top.gochiusa.glplayer.entity.MediaCodecConfiguration
import top.gochiusa.glplayer.util.Constants
import top.gochiusa.glplayer.util.PlayerLog
import java.nio.ByteBuffer

class MediaCodecAudioRenderer(
    renderTimeLimitMs: Long = 5L,
): MediaCodecRenderer(Constants.TRACK_TYPE_AUDIO, renderTimeLimitMs) {

    private val audioClock: MediaClock by lazy { AudioClock() }

    private var audioTrack: AudioTrack? = null

    private var audioFormat: Format? = null

    override fun onSenderChanged(
        format: List<Format>,
        oldSender: Sender?,
        newSender: Sender?,
        startPositionUs: Long
    ) {
        super.onSenderChanged(format, oldSender, newSender, startPositionUs)
        val cacheFormat = audioFormat
        var newFormat: Format?
        if (cacheFormat != null) {
            oldSender?.unbindTrack(cacheFormat, this)
            releaseCodec()
            newFormat = format.firstOrNull {
                it.isAudio() && it.sampleMimeType == cacheFormat.sampleMimeType
                    && it.channelCount== cacheFormat.channelCount
                    && it.sampleRate == cacheFormat.sampleRate
            }
            // 不管newFormat是否为null，更新audioFormat
            audioFormat = newFormat
            if (newFormat == null) {
                releaseAudioTrack()
            } else {
                // 如果该轨道的音频数据与之前的音频数据关于AudioTrack的参数一致，不需要release
                // 启用相关数据的读取
                newSender?.bindTrack(newFormat, this)
                return
            }
        }

        newFormat = format.firstOrNull { it.isAudio() }
        if (newFormat == null) {
            // 将播放位置重置为无效位置
            lastPositionUs = -1L
            // 找不到音频轨道
            PlayerLog.w(
                message = "The media file does not contain media types supported by " +
                        "MediaCodecAudioRenderer"
            )
        } else {
            audioFormat = newFormat
            // 启用相关数据的读取
            newSender?.bindTrack(newFormat, this)
            createAudioTrack(newFormat)
        }
    }

    override fun processOutputBuffer(
        positionUs: Long,
        elapsedRealtimeUs: Long,
        codec: MediaCodec,
        buffer: ByteBuffer?,
        bufferIndex: Int,
        bufferInfo: MediaCodec.BufferInfo
    ): Boolean {
        val bufferSize = bufferInfo.size
        val audioOutput = audioTrack
        //PlayerLog.d(message = "audio position $positionUs, bufferTime ${bufferInfo.presentationTimeUs}")
        if (audioOutput == null || bufferSize <= 0 || buffer == null) {
            buffer?.clear()
            codec.releaseOutputBuffer(bufferIndex, false)
            return true
        }

        try {
            val writeSize = audioOutput.write(buffer.duplicate(), bufferSize,
                AudioTrack.WRITE_NON_BLOCKING)
            audioOutput.play()

            return if (writeSize < bufferSize) {
                bufferInfo.offset += writeSize
                bufferInfo.size -= writeSize
                false
            } else {
                codec.releaseOutputBuffer(bufferIndex, false)
                true
            }
        } catch (e: Exception) {
            PlayerLog.e(message = e)
            codec.releaseOutputBuffer(bufferIndex, false)
            return true
        }
    }

    override fun getMediaCodecConfiguration(): MediaCodecConfiguration? {
        return audioFormat?.sampleMimeType?.let {
            MediaCodecConfiguration(
                type = it,
                format = audioFormat?.mediaFormat,
                surface = null,
                crypto = null,
                flags = 0
            )
        }
    }

    override fun getFormat(): Format? = audioFormat

    override fun getMediaClock(): MediaClock {
        return audioClock
    }

    override fun onPause() {
        runCatching {
            audioTrack?.run {
                if (state == AudioTrack.STATE_INITIALIZED) {
                    pause()
                }
            }
        }
    }

    override fun onDisabled(oldSender: Sender?) {
        super.onDisabled(oldSender)
        runCatching {
            audioTrack?.flush()
        }
    }

    override fun onSeekTo(startPositionUs: Long) {
        super.onSeekTo(startPositionUs)
        runCatching {
            audioTrack?.flush()
        }
    }

    private fun releaseAudioTrack() {
        audioTrack?.release()
        audioTrack = null
    }

    private fun createAudioTrack(format: Format) {
        if (audioTrack == null) {
            val channelConfiguration = if (format.channelCount == 1)
                AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO
            val encoding = AudioFormat.ENCODING_PCM_16BIT

            val minSize = AudioTrack.getMinBufferSize(
                format.sampleRate,
                channelConfiguration,
                encoding
            )
            val attribute: AudioAttributes = AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                .build()
            val audioFormat = AudioFormat.Builder()
                .setSampleRate(format.sampleRate)
                .setChannelMask(channelConfiguration)
                .setEncoding(encoding)
                .build()

            audioTrack = AudioTrack(
                attribute, audioFormat, minSize * 2,
                AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE
            )
        }
    }

    inner class AudioClock: MediaClock {

        override fun getPositionUs(): Long = lastPositionUs

        override fun getDurationUs(): Long {
            return audioFormat?.duration ?: -1L
        }
    }
}