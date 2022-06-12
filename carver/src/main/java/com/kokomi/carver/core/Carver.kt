package com.kokomi.carver.core

import android.os.Handler
import android.os.Looper


class Carver<P, C> private constructor(
    captor: Captor<P, C>,
    listener: CarverListener?
) {

    companion object {
        private const val TAG = "Carver"
    }

    private var mCaptor = captor

    private val mListener = listener

    init {
        mCaptor.attachTo(this)
    }

    fun shutdown() {
        mCaptor.shutdown()
    }

    fun bindPreview(preview: P) {
        mCaptor.bindPreview(preview)
    }

    fun prepare() {
        mCaptor.prepare()
    }

    fun start() {
        mCaptor.start()
    }

    fun stop() {
        mCaptor.pause()
    }

    fun pause() {
        mCaptor.pause()
    }

    fun resume() {
        mCaptor.resume()
    }

    internal fun onStatusChanged(status: CarverStatus) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            mListener?.onStatusChanged(status)
        } else {
            Handler(Looper.getMainLooper()).post {
                mListener?.onStatusChanged(status)
            }
        }
    }

    class Builder<P, C>(captor: Captor<P, C>) {

        private val mCaptor = captor

        private var mListener: CarverListener? = null

        fun build(): Carver<P, C> {
            return Carver(mCaptor, mListener)
        }

        fun setListener(listener: CarverListener) = apply {
            mListener = listener
        }

        fun setListener(listener: (CarverStatus) -> Unit) = apply {
            mListener = object : CarverListener {
                override fun onStatusChanged(status: CarverStatus) = listener(status)
            }
        }
    }

}