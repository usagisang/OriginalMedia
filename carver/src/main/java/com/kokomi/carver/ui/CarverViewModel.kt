package com.kokomi.carver.ui

import androidx.lifecycle.ViewModel
import com.kokomi.carver.core.Captor
import com.kokomi.carver.core.Carver
import com.kokomi.carver.core.CarverStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CarverViewModel : ViewModel() {

    private val _carverStatus = MutableStateFlow<CarverStatus>(CarverStatus.Initial())
    val carverStatus: StateFlow<CarverStatus> = _carverStatus

    private val _recordingStatus = MutableStateFlow<CarverStatus.Recording<*>?>(null)
    val recordingStatus: StateFlow<CarverStatus.Recording<*>?> = _recordingStatus

    internal fun <P, C> createCarver(captor: Captor<P, C>): Carver<P, C> {
        return Carver(captor) { status ->
            if (status is CarverStatus.Recording<*>) {
                _recordingStatus.value = status
            } else {
                _carverStatus.value = status
            }
        }
    }

}