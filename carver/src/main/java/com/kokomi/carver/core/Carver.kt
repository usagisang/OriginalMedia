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

    fun newConfig(config: C) {
        captor.onConfigurationChanged(config)
    }

    fun getConfig(): C {
        return captor.getConfig()
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

    fun changeLensFacing() {
        captor.changeLensFacing()
    }

    fun setZoom(zoom: Float) {
        captor.zoom(zoom)
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