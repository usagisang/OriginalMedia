package com.kokomi.carver.core

import android.os.Handler
import android.os.Looper
import androidx.camera.core.ZoomState
import androidx.lifecycle.LiveData

class Carver<P, C, Z>(
    private val captor: Captor<P, C, Z>,
    private val listener: (CarverStatus) -> Unit
) {

    init {
        captor.attachTo(this)
    }

    fun shutdown() {
        captor.shutdown()
    }

    fun config(config: C) {
        captor.onConfigurationChanged(config)
    }

    fun config(): C {
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

    fun lensFacing() {
        captor.changeLensFacing()
    }

    fun zoom(): LiveData<Z> {
        return captor.zoom()
    }

    fun zoom(zoom: Float) {
        captor.zoom(zoom)
    }

    fun focus(x: Float, y: Float) {
        captor.focus(x, y)
    }

    fun cancelFocus() {
        captor.cancelFocus()
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