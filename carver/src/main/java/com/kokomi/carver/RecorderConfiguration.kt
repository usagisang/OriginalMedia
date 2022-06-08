package com.kokomi.carver

class RecorderConfiguration private constructor(
    internal val mVideoFrameRate: Int,
    internal val mVideoBitRate: Int,
    internal var mVideoFrameInterval: Int,
    internal var mAudioBitRate: Int
) {

    companion object {
        internal const val DEFAULT = Int.MIN_VALUE
    }

    class Builder {

        private var mVideoFrameRate = DEFAULT

        private var mVideoBitRate = DEFAULT

        private var mVideoFrameInterval = DEFAULT

        private var mAudioBitRate = DEFAULT

        fun build() = RecorderConfiguration(
            mVideoFrameRate,
            mVideoBitRate,
            mVideoFrameInterval,
            mAudioBitRate
        )

        fun setVideoFrameRate(videoFrameRate: Int) = apply {
            if (videoFrameRate > 0) {
                mVideoFrameRate = videoFrameRate
            } else {
                throw IllegalArgumentException("Video frame rate cannot be non positive.")
            }
        }

        fun setVideoBitRate(videoBitRate: Int) = apply {
            if (videoBitRate > 0) {
                mVideoFrameRate = videoBitRate
            } else {
                throw IllegalArgumentException("Video bit rate cannot be non positive.")
            }
        }

        fun setVideoIFrameInterval(interval: Int) = apply {
            mVideoFrameInterval = interval
        }

        fun setAudioBitRate(audioBitRate: Int) = apply {
            if (audioBitRate > 0) {
                mAudioBitRate = audioBitRate
            } else {
                throw IllegalArgumentException("Audio bit rate cannot be non positive.")
            }
        }

    }

}