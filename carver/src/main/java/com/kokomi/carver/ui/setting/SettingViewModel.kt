package com.kokomi.carver.ui.setting

import androidx.lifecycle.ViewModel
import com.kokomi.carver.core.camerax.getSupportedQualities
import com.kokomi.carver.core.camerax.qualityFormatter
import com.kokomi.carver.ui.capture.CAMERAX_VIDEO_IMPL
import kotlinx.coroutines.flow.MutableStateFlow

internal const val IMPL = "impl"
internal const val QUALITY = "resolving_power"
internal const val VIDEO_FRAME_RATE = "video_frame_rate"
internal const val BIT_RATE = "bit_rate"
internal const val I_FRAME_INTERVAL = "i_frame_interval"
internal const val AUDIO_SAMPLE_RATE = "audio_sample_rate"
internal const val AUDIO_BIT_RATE = "audio_bit_rate"
internal const val AUDIO_CHANNEL_COUNT = "audio_channel_count"

class SettingViewModel : ViewModel() {

    val impl = MutableStateFlow(CAMERAX_VIDEO_IMPL)

    val selectedImpl = MutableStateFlow(-1)

    val selectedQuality = MutableStateFlow(-1)

    val videoFrameRate = MutableStateFlow(-1)

    val bitRate = MutableStateFlow(-1)

    val iFrameInterval = MutableStateFlow(-1)

    val audioSampleRate = MutableStateFlow(-1)

    val audioBitRate = MutableStateFlow(-1)

    val audioChannelCount = MutableStateFlow(-1)

    val supportedQuality: List<String> = getSupportedQualities().let { qualities ->
        val list = mutableListOf<String>()
        qualities.map { quality ->
            list.add(qualityFormatter(quality))
        }
        list
    }

}