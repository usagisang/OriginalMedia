package com.kokomi.carver.core

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.view.TextureView
import androidx.annotation.RequiresApi

abstract class MediaCaptorImpl(mContext: Context) : Captor<TextureView>() {

    private val mMediaRecorder by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(mContext)
        } else {
            @Suppress("Deprecation")
            MediaRecorder()
        }
    }

    override fun shutdown() {
        mMediaRecorder.release()
    }

    override fun configure(configuration: RecorderConfiguration) {
        mMediaRecorder.reset()
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        with(configuration) {
            mMediaRecorder.setVideoFrameRate(mVideoFrameRate)
            mMediaRecorder.setVideoEncodingBitRate(mVideoBitRate)
            mMediaRecorder.setAudioEncodingBitRate(mAudioBitRate)
        }
        mMediaRecorder.prepare()
    }

    override fun prepare() {
        mMediaRecorder.prepare()
    }

    override fun reset() {
        mMediaRecorder.reset()
    }

    override fun start() {
        mMediaRecorder.start()
    }

    override fun stop() {
        mMediaRecorder.stop()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun pause() {
        mMediaRecorder.pause()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun resume() {
        mMediaRecorder.resume()
    }

}