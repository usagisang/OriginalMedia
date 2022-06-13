package com.kokomi.carver.core

import android.os.Handler
import android.os.Looper

class Carver<P, C>(
    private val captor: Captor<P, C>,
    private val listener: (CarverStatus) -> Unit
) {

    companion object {
        private const val TAG = "Carver"
    }

    init {
        captor.attachTo(this)
    }

    fun shutdown() {
        captor.shutdown()
    }

    fun bindPreview(preview: P) {
        captor.bindPreview(preview)
    }

    fun prepare() {
        captor.prepare()
    }

    fun start() {
        captor.start()
    }

    fun stop() {
        captor.stop()
    }

    fun pause() {
        captor.pause()
    }

    fun resume() {
        captor.resume()
    }

    internal fun onStatusChanged(status: CarverStatus) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            listener(status)
        } else {
            Handler(Looper.getMainLooper()).post {
                listener(status)
            }
        }
    }

}