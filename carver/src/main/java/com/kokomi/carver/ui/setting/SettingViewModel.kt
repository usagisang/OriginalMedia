package com.kokomi.carver.ui.setting

import androidx.lifecycle.ViewModel
import com.kokomi.carver.core.camerax.getSupportedQualities
import com.kokomi.carver.core.camerax.qualityFormatter
import kotlinx.coroutines.flow.MutableStateFlow

class SettingViewModel : ViewModel() {

    val impl = MutableStateFlow("camerax")

    val selectedResolvingPower = MutableStateFlow(-1)

    val supportedQuality: List<String> = getSupportedQualities().let { qualities ->
        val list = mutableListOf<String>()
        qualities.map { quality ->
            list.add(qualityFormatter(quality))
        }
        list
    }

}