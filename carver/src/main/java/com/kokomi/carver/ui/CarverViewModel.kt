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

    internal fun <P, C> createCarver(captor: Captor<P, C>): Carver<P, C> {
        return Carver.Builder(captor)
            .setListener { status ->
                _carverStatus.value = status
            }
            .build()
    }

}