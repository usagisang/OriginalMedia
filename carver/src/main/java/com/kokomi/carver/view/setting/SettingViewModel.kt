package com.kokomi.carver.view.setting

import androidx.lifecycle.ViewModel
import com.kokomi.carver.core.camerax.getSupportedQualities
import com.kokomi.carver.core.camerax.qualityFormatter
import com.kokomi.carver.view.carver.CAMERAX_VIDEO_IMPL
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

    /**
     * 设置参数，高级模式或是普通模式
     * */
    val impl = MutableStateFlow(CAMERAX_VIDEO_IMPL)

    val selectedImpl = MutableStateFlow(-1)

    /**
     * 设置参数，选择的录制视频质量（仅在普通模式生效）
     * */
    val selectedQuality = MutableStateFlow(-1)

    /**
     * 设置参数，视频帧率（仅在高级模式生效）
     * */
    val videoFrameRate = MutableStateFlow(-1)

    /**
     * 设置参数，视频码率（仅在高级模式生效）
     * */
    val bitRate = MutableStateFlow(-1)

    /**
     * 设置参数，I 帧间隔（仅在高级模式生效）
     * */
    val iFrameInterval = MutableStateFlow(-1)

    /**
     * 设置参数，音频采样率（仅在高级模式生效）
     * */
    val audioSampleRate = MutableStateFlow(-1)

    /**
     * 设置参数，音频比特率（仅在高级模式生效）
     * */
    val audioBitRate = MutableStateFlow(-1)

    /**
     * 设置参数，视频通道数（仅在高级模式生效）
     * */
    val audioChannelCount = MutableStateFlow(-1)

    /**
     * 获取支持的视频质量列表
     * */
    val supportedQuality: List<String> = getSupportedQualities().let { qualities ->
        val list = mutableListOf<String>()
        qualities.map { quality ->
            list.add(qualityFormatter(quality))
        }
        list
    }

}