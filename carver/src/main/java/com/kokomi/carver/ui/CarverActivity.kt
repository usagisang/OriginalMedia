package com.kokomi.carver.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.video.OutputResults
import androidx.camera.video.RecordingStats
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kokomi.carver.*
import com.kokomi.carver.clearSystemWindows
import com.kokomi.carver.core.*
import com.kokomi.carver.core.camerax.CameraXCaptorImpl
import com.kokomi.carver.core.camerax.CameraXConfiguration
import com.kokomi.carver.formatRecordingTime
import com.kokomi.carver.setStatusBarTextColor
import com.kokomi.carver.weight.ZoomGestureView
import kotlinx.coroutines.launch

class CarverActivity : AppCompatActivity() {

    private var saving = false

    private lateinit var carver: Carver<PreviewView, CameraXConfiguration>

    private lateinit var statusBar: TextView
    private lateinit var timeText: TextView
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
                } else if (result.key == carverPermissions[1]) {
                    if (!result.value) {
                        Toast.makeText(this, "未授予录音权限，无法录制音频", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        init()
                    }
                }
            }
        }
    }

    private fun init() {
        vm = ViewModelProvider(this)[CarverViewModel::class.java]

        timeText = findViewById(R.id.tv_carver_time)
        previewView = findViewById(R.id.pv_carver_preview)
        blurEffect = findViewById(R.id.iv_carver_blur_effect)
        zoomGesture = findViewById(R.id.zgv_carver_zoom_gesture)
        control = findViewById(R.id.cv_carver_control)
        changeCamera = findViewById(R.id.cv_carver_change_camera)

        carver = vm.createCarver(CameraXCaptorImpl(this))

        carver.bindPreview(previewView)
        carver.prepare()

        zoomGesture.setZoomChangedListener {
            val status = vm.carverStatus.value
            if (!(status is CarverStatus.Initial || status is CarverStatus.Error || status is CarverStatus.Shutdown)) {
                carver.setZoom(it)
            }
        }

        control.setOnClickListener {
            if (saving) {
                toast("正在保存，请稍后")
                return@setOnClickListener
            }
            when (vm.carverStatus.value) {
                is CarverStatus.Finalize<*>, is CarverStatus.Prepared -> {
                    carver.start()
                }
                is CarverStatus.Start, is CarverStatus.Resume -> {
                    carver.pause()
                }
                is CarverStatus.Pause -> {
                    carver.resume()
                }
                else -> {
                    /* Nothing to do. */
                }
            }
        }

        control.setOnLongClickListener {
            val status = vm.carverStatus.value
            if (status is CarverStatus.Start || status is CarverStatus.Resume) {
                saving = true
                toast("正在保存")
                carver.stop()
            }
            true
        }

        changeCamera.setOnClickListener {
            val status = vm.carverStatus.value
            if (!(status is CarverStatus.Prepared || status is CarverStatus.Finalize<*>))
                return@setOnClickListener
            val lensFacing = if (carver.getConfig().lensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            carver.newConfig(carver.getConfig().copy(lensFacing = lensFacing))
        }

        lifecycleScope.launch {
            vm.carverStatus.collect { status ->
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
        carver.shutdown()
    }

}