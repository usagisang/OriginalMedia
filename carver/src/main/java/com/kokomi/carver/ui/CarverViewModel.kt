package com.kokomi.carver.ui

import androidx.lifecycle.ViewModel
import com.kokomi.carver.core.CarverStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CarverViewModel : ViewModel() {

    private val _carverStatus = MutableStateFlow(CarverStatus.Initial())
    val carverStatus: StateFlow<CarverStatus> = _carverStatus

}