package com.kokomi.carver.ui.capture

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kokomi.carver.*
import com.kokomi.carver.clearSystemWindows
import com.kokomi.carver.core.*
import com.kokomi.carver.core.camerax.CameraXCaptorImpl
import com.kokomi.carver.core.camerax.getSupportedQualities
import com.kokomi.carver.core.camerax.qualityFormatter
import com.kokomi.carver.formatRecordingTime
import com.kokomi.carver.setStatusBarTextColor
import com.kokomi.carver.ui.setting.*
import com.kokomi.carver.ui.setting.BIT_RATE
import com.kokomi.carver.ui.setting.I_FRAME_INTERVAL
import com.kokomi.carver.ui.setting.QUALITY
import com.kokomi.carver.ui.setting.VIDEO_FRAME_RATE
import com.kokomi.carver.weight.ZoomGestureView
import kotlinx.coroutines.launch

class CarverActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CarverActivity"
    }

    private val resultFromSettingActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.run {
                    vm.updateCarver(
                        getStringExtra(IMPL) ?: CAMERAX_IMPL,
                        this@CarverActivity,
                        previewView
                    )
                    val config = vm.carver.getConfig()
                    val qualityIndex = getIntExtra(QUALITY, -1)
                    val videoFrameRate = getIntExtra(VIDEO_FRAME_RATE, -1)
                    val bitRate = getIntExtra(BIT_RATE, -1)
                    val iFrameInterval = getIntExtra(I_FRAME_INTERVAL, -1)
                    val audioSampleRate = getIntExtra(AUDIO_SAMPLE_RATE, -1)
                    val audioBitRate = getIntExtra(AUDIO_BIT_RATE, -1)
                    val audioChannelCount = getIntExtra(AUDIO_CHANNEL_COUNT, -1)
                    vm.carver.newConfig(
                        config.copy(
                            quality = if (qualityIndex != -1) getSupportedQualities()[qualityIndex] else config.quality,
                            videoFrameRate = videoFrameRate,
                            bitRate = bitRate,
                            iFrameInterval = iFrameInterval,
                            audioSampleRate = audioSampleRate,
                            audioBitRate = audioBitRate,
                            audioChannelCount = audioChannelCount
                        )
                    )
                }
            }
        }

    private var saving = false

    private lateinit var statusBar: TextView
    private lateinit var rec: TextView
    private lateinit var timeText: TextView
    private lateinit var setting: ImageView
    private lateinit var previewView: PreviewView
    private lateinit var blurEffect: ImageView
    private lateinit var zoomGesture: ZoomGestureView
    private lateinit var control: CardView
    private lateinit var changeCamera: CardView

    private lateinit var vm: CarverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carver)

        statusBar = findViewById(R.id.tv_carver_status_bar)

        requestCarverPermissions {
            it.map { result ->
                if (result.key == carverPermissions[0]) {
                    if (!result.value) {
                        Toast.makeText(this, "未授予拍照权限，无法录制视频", Toast.LENGTH_LONG).show()
                        finish()
                        return@map
                    }
                }
                if (result.key == carverPermissions[1]) {
                    if (!result.value) {
                        Toast.makeText(this, "未授予录音权限，无法录制音频", Toast.LENGTH_LONG).show()
                        finish()
                        return@map
                    } else {
                        init()
                    }
                }
            }
        }
    }

    private fun init() {
        vm = ViewModelProvider(this)[CarverViewModel::class.java]

        rec = findViewById(R.id.tv_carver_rec)
        timeText = findViewById(R.id.tv_carver_time)
        setting = findViewById(R.id.iv_carver_setting)
        previewView = findViewById(R.id.pv_carver_preview)
        blurEffect = findViewById(R.id.iv_carver_blur_effect)
        zoomGesture = findViewById(R.id.zgv_carver_zoom_gesture)
        control = findViewById(R.id.cv_carver_control)
        changeCamera = findViewById(R.id.cv_carver_change_camera)

        vm.createCarver(CameraXCaptorImpl(this), previewView)

        setting.setOnClickListener {
            resultFromSettingActivity.launch(
                Intent(this, SettingActivity::class.java).apply {
                    val config = vm.carver.getConfig()
                    putExtra(IMPL, vm.impl.value)
                    putExtra(QUALITY, qualityFormatter(vm.carver.getConfig().quality))
                    putExtra(VIDEO_FRAME_RATE, config.videoFrameRate)
                    putExtra(BIT_RATE, config.bitRate)
                    putExtra(I_FRAME_INTERVAL, config.iFrameInterval)
                    putExtra(AUDIO_SAMPLE_RATE, config.audioSampleRate)
                    putExtra(AUDIO_BIT_RATE, config.audioBitRate)
                    putExtra(AUDIO_CHANNEL_COUNT, config.audioChannelCount)
                })
        }

        zoomGesture.setZoomChangedListener {
            val status = this.vm.carverStatus.value
            if (!(status is CarverStatus.Initial || status is CarverStatus.Error || status is CarverStatus.Shutdown)) {
                vm.carver.setZoom(it)
            }
        }

        control.setOnClickListener {
            if (saving) {
                toast("正在保存，请稍后")
                return@setOnClickListener
            }
            when (this.vm.carverStatus.value) {
                is CarverStatus.Finalize, is CarverStatus.Prepared -> {
                    vm.carver.start()
                }
                is CarverStatus.Start, is CarverStatus.Resume -> {
                    vm.carver.pause()
                }
                is CarverStatus.Pause -> {
                    vm.carver.resume()
                }
                else -> {
                    /* Nothing to do. */
                }
            }
        }

        control.setOnLongClickListener {
            val status = this.vm.carverStatus.value
            if (status is CarverStatus.Start || status is CarverStatus.Resume || status is CarverStatus.Pause) {
                saving = true
                toast("正在保存")
                vm.carver.stop()
            }
            true
        }

        changeCamera.setOnClickListener {
            val status = this.vm.carverStatus.value
            if (!(status is CarverStatus.Prepared || status is CarverStatus.Finalize))
                return@setOnClickListener
            vm.carver.changeLensFacing()
        }

        lifecycleScope.launch {
            this@CarverActivity.vm.carverStatus.collect { status ->
                when (status) {
                    is CarverStatus.Initial -> {
                        val b = previewView.bitmap ?: return@collect
                        PreviewViewBlurEffect(this@CarverActivity, b, blurEffect).startAnim()
                        rec.text = "Initial"
                    }
                    is CarverStatus.Prepared -> {
                        rec.text = "Prepared"
                    }
                    is CarverStatus.Start -> {
                        startRecBreath()
                        rec.text = "REC"
                    }
                    is CarverStatus.Finalize -> {
                        stopRecBreath()
                        with(status.info) {
                            intent.putExtra("video_file", path)
                            setResult(RESULT_OK, intent)
                            toast("录像已保存至:${path ?: "保存错误"}")
                        }
                        timeText.text = "--:--"
                        saving = false
                        rec.text = "Finalize"
                    }
                    is CarverStatus.Pause -> {
                        stopRecBreath()
                        rec.text = "Pause"
                    }
                    is CarverStatus.Resume -> {
                        startRecBreath()
                        rec.text = "REC"
                    }
                    is CarverStatus.Shutdown -> {
                        rec.text = "Shutdown"
                    }
                    is CarverStatus.Error -> {
                        rec.text = "ERROR"
                        Log.e(TAG, "[ERROR]", status.t)
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            vm.recordingStatus.collect { status ->
                status ?: return@collect
                timeText.text = formatRecordingTime(status.info)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        clearSystemWindows(statusBar)
        setStatusBarTextColor(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.carver.shutdown()
    }

    private fun startRecBreath() {
        ObjectAnimator.ofFloat(
            rec,
            "alpha",
            1f,
            0f,
            1f
        ).apply {
            setAutoCancel(true)
            duration = 2000L
            repeatCount = ObjectAnimator.INFINITE
        }.start()
    }

    private fun stopRecBreath() {
        ObjectAnimator.ofFloat(
            rec,
            "alpha",
            rec.alpha,
            1f
        ).apply {
            setAutoCancel(true)
            duration = 100L
        }.start()
    }

}