package com.kokomi.carver.ui.capture

import androidx.camera.view.PreviewView
import androidx.lifecycle.ViewModel
import com.kokomi.carver.core.Captor
import com.kokomi.carver.core.Carver
import com.kokomi.carver.core.CarverStatus
import com.kokomi.carver.core.camerax.CameraXCaptorImpl
import com.kokomi.carver.core.camerax.CameraXConfiguration
import com.kokomi.carver.core.camerax.CameraXCoreCaptorImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal const val CAMERAX_IMPL = "camerax"
internal const val CAMERAX_CORE_IMPL = "camerax_core"

internal val IMPL_LIST = listOf(CAMERAX_IMPL, CAMERAX_CORE_IMPL)

class CarverViewModel : ViewModel() {

    private val _impl = MutableStateFlow(CAMERAX_IMPL)
    val impl: StateFlow<String> = _impl

    internal lateinit var carver: Carver<PreviewView, CameraXConfiguration>

    private val _carverStatus = MutableStateFlow<CarverStatus>(CarverStatus.Initial())
    val carverStatus: StateFlow<CarverStatus> = _carverStatus

    private val _recordingStatus = MutableStateFlow<CarverStatus.Recording?>(null)
    val recordingStatus: StateFlow<CarverStatus.Recording?> = _recordingStatus

    internal fun updateCarver(newImpl: String, activity: CarverActivity, preview: PreviewView) {
        if (newImpl != _impl.value) {
            carver.shutdown()
            _impl.value = newImpl
            val captor = if (_impl.value == CAMERAX_CORE_IMPL)
                CameraXCoreCaptorImpl(activity)
            else
                CameraXCaptorImpl(activity)
            Carver(captor) { status ->
                if (status is CarverStatus.Recording) {
                    _recordingStatus.value = status
                } else {
                    _carverStatus.value = status
                }
            }.apply {
                bindPreview(preview)
                prepare()
                carver = this
            }
        }
    }

    internal fun createCarver(
        captor: Captor<PreviewView, CameraXConfiguration>,
        preview: PreviewView
    ) {
        Carver(captor) { status ->
            if (status is CarverStatus.Recording) {
                _recordingStatus.value = status
            } else {
                _carverStatus.value = status
            }
        }.apply {
            bindPreview(preview)
            prepare()
            carver = this
        }
    }

}