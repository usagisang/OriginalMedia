package com.kokomi.carver.ui.capture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.video.OutputResults
import androidx.camera.video.Quality
import androidx.camera.video.RecordingStats
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
import com.kokomi.carver.ui.setting.SettingActivity
import com.kokomi.carver.weight.ZoomGestureView
import kotlinx.coroutines.launch

class CarverActivity : AppCompatActivity() {

    private val resultFromSettingActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.getIntExtra("resolving_power", 0)?.let { quality ->
                    val supportedQualities = getSupportedQualities()
                    vm.carver.newConfig(
                        vm.carver.getConfig().copy(quality = supportedQualities[quality])
                    )
                }
            }
        }

    private var saving = false

    private lateinit var statusBar: TextView
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
        vm.createCarver(CameraXCaptorImpl(this))

        timeText = findViewById(R.id.tv_carver_time)
        setting = findViewById(R.id.iv_carver_setting)
        previewView = findViewById(R.id.pv_carver_preview)
        blurEffect = findViewById(R.id.iv_carver_blur_effect)
        zoomGesture = findViewById(R.id.zgv_carver_zoom_gesture)
        control = findViewById(R.id.cv_carver_control)
        changeCamera = findViewById(R.id.cv_carver_change_camera)

        vm.carver.bindPreview(previewView)
        vm.carver.prepare()

        setting.setOnClickListener {
            resultFromSettingActivity.launch(
                Intent(this, SettingActivity::class.java).apply {
                    putExtra("impl", "camerax")
                    putExtra("resolving_power", qualityFormatter(vm.carver.getConfig().quality))
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
                is CarverStatus.Finalize<*>, is CarverStatus.Prepared -> {
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
            if (!(status is CarverStatus.Prepared || status is CarverStatus.Finalize<*>))
                return@setOnClickListener
            vm.carver.changeLensFacing()
        }

        lifecycleScope.launch {
            this@CarverActivity.vm.carverStatus.collect { status ->
                when (status) {
                    is CarverStatus.Initial -> {
                        val b = previewView.bitmap ?: return@collect
                        PreviewViewBlurEffect(this@CarverActivity, b, blurEffect).startAnim()
                    }
                    is CarverStatus.Prepared -> {
                    }
                    is CarverStatus.Start -> {
                        toast("开始")
                    }
                    is CarverStatus.Finalize<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        status as CarverStatus.Finalize<OutputResults>
                        with(status.info) {
                            toast("录像已保存至:${outputUri.path ?: "保存错误"}")
                        }
                        timeText.text = "--:--"
                        saving = false
                    }
                    is CarverStatus.Pause -> {
                        toast("已暂停")
                    }
                    is CarverStatus.Resume -> {
                        toast("继续")
                    }
                    is CarverStatus.Shutdown -> {}
                    is CarverStatus.Error -> {
                        toast("发生错误")
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            vm.recordingStatus.collect { status ->
                status ?: return@collect
                @Suppress("UNCHECKED_CAST")
                status as CarverStatus.Recording<RecordingStats>
                with(status.info) {
                    timeText.text = formatRecordingTime(recordedDurationNanos)
                }
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

}