package com.kokomi.carver.ui.setting

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.kokomi.carver.setStatusBarTextColor
import com.kokomi.carver.ui.capture.CAMERAX_VIDEO_IMPL
import com.kokomi.carver.ui.capture.IMPL_LIST

/**
 * 设置界面 Activity
 *
 * @see [SettingContentView]
 * */
class SettingActivity : ComponentActivity() {

    private lateinit var vm: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParams()
        setContent {
            CarverTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SoftBlack
                ) {
                    SettingContentView(vm = vm)
                }
            }
        }
    }

    // 初始化 ViewModel 数据
    private fun initParams() {
        vm = ViewModelProvider(this)[SettingViewModel::class.java]
        vm.impl.value = intent.getStringExtra(IMPL) ?: CAMERAX_VIDEO_IMPL
        vm.selectedImpl.value = IMPL_LIST.indexOf(vm.impl.value)
        vm.selectedQuality.value = vm.supportedQuality.indexOf(intent.getStringExtra(QUALITY) ?: 0)
        vm.videoFrameRate.value = intent.getIntExtra(VIDEO_FRAME_RATE, -1)
        vm.bitRate.value = intent.getIntExtra(BIT_RATE, -1)
        vm.iFrameInterval.value = intent.getIntExtra(I_FRAME_INTERVAL, -1)
        vm.audioSampleRate.value = intent.getIntExtra(AUDIO_SAMPLE_RATE, -1)
        vm.audioBitRate.value = intent.getIntExtra(AUDIO_BIT_RATE, -1)
        vm.audioChannelCount.value = intent.getIntExtra(AUDIO_CHANNEL_COUNT, -1)
    }

    override fun onResume() {
        super.onResume()
        window.statusBarColor = Color.rgb(0x21, 0x21, 0x21)
        setStatusBarTextColor(true)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishInternal()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    // Activity 结束时提交数据
    private fun finishInternal() {
        vm.impl.value = IMPL_LIST[vm.selectedImpl.value]
        intent.putExtra(IMPL, vm.impl.value)
        intent.putExtra(QUALITY, vm.selectedQuality.value)
        intent.putExtra(VIDEO_FRAME_RATE, vm.videoFrameRate.value)
        intent.putExtra(BIT_RATE, vm.bitRate.value)
        intent.putExtra(I_FRAME_INTERVAL, vm.iFrameInterval.value)
        intent.putExtra(AUDIO_SAMPLE_RATE, vm.audioSampleRate.value)
        intent.putExtra(AUDIO_BIT_RATE, vm.audioBitRate.value)
        intent.putExtra(AUDIO_CHANNEL_COUNT, vm.audioChannelCount.value)
        setResult(RESULT_OK, intent)
        finish()
    }

}

