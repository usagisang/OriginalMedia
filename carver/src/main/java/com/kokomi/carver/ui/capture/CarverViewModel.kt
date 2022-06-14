package com.kokomi.carver.ui.capture

import androidx.camera.view.PreviewView
import androidx.lifecycle.ViewModel
import com.kokomi.carver.core.Captor
import com.kokomi.carver.core.Carver
import com.kokomi.carver.core.CarverStatus
import com.kokomi.carver.core.camerax.CameraXConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CarverViewModel : ViewModel() {

    internal lateinit var carver: Carver<PreviewView, CameraXConfiguration>

    private val _carverStatus = MutableStateFlow<CarverStatus>(CarverStatus.Initial())
    val carverStatus: StateFlow<CarverStatus> = _carverStatus

    private val _recordingStatus = MutableStateFlow<CarverStatus.Recording<*>?>(null)
    val recordingStatus: StateFlow<CarverStatus.Recording<*>?> = _recordingStatus

    internal fun createCarver(captor: Captor<PreviewView, CameraXConfiguration>) {
        Carver(captor) { status ->
            if (status is CarverStatus.Recording<*>) {
                _recordingStatus.value = status
            } else {
                _carverStatus.value = status
            }
        }.apply { carver = this }
    }

}