package com.kokomi.carver.view.carver

import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kokomi.carver.*
import com.kokomi.carver.core.CarverStatus
import com.kokomi.carver.core.camerax.CameraXVideoCaptorImpl
import com.kokomi.carver.core.camerax.getSupportedQualities
import com.kokomi.carver.core.camerax.qualityFormatter
import com.kokomi.carver.view.setting.*
import com.kokomi.carver.weight.CircleProgressBar
import com.kokomi.carver.weight.GestureView
import com.kokomi.okpremission.OkResult.requirePermission
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.round

/**
 * 使用 [CarverActivity] 可进行视频录制
 *
 * <p>
 *
 * 当 [CarverActivity] 结束时，若用户已经录制了视频，则会调用 [setResult] 函数将返回码设为
 * [AppCompatActivity.RESULT_OK] ，然后在返回的 Intent 中的 video_file 字段存放视频文件的保存路径
 *
 * <p>
 *
 * 若用户没有录制视频就退出了 [CarverActivity] ，则不会执行任何操作，直接结束 [CarverActivity]
 *
 * */
class CarverActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CarverActivity"

        /**
         * 最大录制时间，单位：纳秒
         * */
        private const val MAX_RECORDING_TIME = 60_000_000_000L

        /**
         * 录制要请求的权限列表
         * */
        private val carverPermissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
    }

    private val resultFromSettingActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.run {
                    vm.updateCarver(
                        getStringExtra(IMPL) ?: CAMERAX_VIDEO_IMPL,
                        this@CarverActivity,
                        previewView
                    )
                    val config = vm.carver.config()
                    val qualityIndex = getIntExtra(QUALITY, -1)
                    val videoFrameRate = getIntExtra(VIDEO_FRAME_RATE, -1)
                    val bitRate = getIntExtra(BIT_RATE, -1)
                    val iFrameInterval = getIntExtra(I_FRAME_INTERVAL, -1)
                    val audioSampleRate = getIntExtra(AUDIO_SAMPLE_RATE, -1)
                    val audioBitRate = getIntExtra(AUDIO_BIT_RATE, -1)
                    val audioChannelCount = getIntExtra(AUDIO_CHANNEL_COUNT, -1)
                    vm.carver.config(
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
    private lateinit var gestureView: GestureView
    private lateinit var zoomText: TextView
    private lateinit var pauseAndResume: CardView
    private lateinit var pauseAndResumeImage: ImageView
    private lateinit var control: CardView
    private lateinit var controlPoint: CardView
    private lateinit var progressBar: CircleProgressBar
    private lateinit var changeCamera: CardView

    private lateinit var vm: CarverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carver)

        statusBar = findViewById(R.id.tv_carver_status_bar)

        var count = 0

        requirePermission(carverPermissions, {
            count++
            if (count == 2) init()
        }, {
            if (it == carverPermissions[0]) {
                toast("未授予拍照权限，无法录制视频")
                finish()
            }
            if (it == carverPermissions[1]) {
                toast("未授予录音权限，无法录制音频")
                finish()
            }
        })
    }

    private fun init() {
        vm = ViewModelProvider(this)[CarverViewModel::class.java]

        rec = findViewById(R.id.tv_carver_rec)
        timeText = findViewById(R.id.tv_carver_time)
        setting = findViewById(R.id.iv_carver_setting)
        previewView = findViewById(R.id.pv_carver_preview)
        blurEffect = findViewById(R.id.iv_carver_blur_effect)
        gestureView = findViewById(R.id.zgv_carver_gesture)
        zoomText = findViewById(R.id.tv_carver_zoom)
        pauseAndResume = findViewById(R.id.cv_carver_pause_and_resume)
        pauseAndResumeImage = findViewById(R.id.iv_carver_pause_and_resume)
        control = findViewById(R.id.cv_carver_control)
        controlPoint = findViewById(R.id.cv_carver_control_point)
        progressBar = findViewById(R.id.cpb_carver_progress)
        changeCamera = findViewById(R.id.cv_carver_change_camera)

        progressBar.run {
            setMaxProgress(MAX_RECORDING_TIME)
            setProgress(MAX_RECORDING_TIME)
            scaleX = 0.6f
            scaleY = 0.6f
        }

        vm.createCarver(CameraXVideoCaptorImpl(this), previewView)

        setting.setOnClickListener {
            resultFromSettingActivity.launch(
                Intent(this, SettingActivity::class.java).apply {
                    val config = vm.carver.config()
                    putExtra(IMPL, vm.impl.value)
                    putExtra(QUALITY, qualityFormatter(vm.carver.config().quality))
                    putExtra(VIDEO_FRAME_RATE, config.videoFrameRate)
                    putExtra(BIT_RATE, config.bitRate)
                    putExtra(I_FRAME_INTERVAL, config.iFrameInterval)
                    putExtra(AUDIO_SAMPLE_RATE, config.audioSampleRate)
                    putExtra(AUDIO_BIT_RATE, config.audioBitRate)
                    putExtra(AUDIO_CHANNEL_COUNT, config.audioChannelCount)
                })
        }

        gestureView.setZoomChangedListener {
            val status = this.vm.carverStatus.value
            if (!(status is CarverStatus.Initial || status is CarverStatus.Error || status is CarverStatus.Shutdown)) {
                vm.carver.zoom(it)
            }
        }

        gestureView.setClickListener { x, y -> vm.carver.focus(x, y) }

        pauseAndResume.setOnClickListener {
            when (this.vm.carverStatus.value) {
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

        control.setOnClickListener {
            if (saving) {
                toast("正在保存，请稍后")
                return@setOnClickListener
            }
            when (this.vm.carverStatus.value) {
                is CarverStatus.Finalize, is CarverStatus.Prepared -> {
                    vm.carver.start()
                }
                is CarverStatus.Start, is CarverStatus.Resume, is CarverStatus.Pause -> {
                    saving = true
                    vm.carver.stop()
                    toast("正在保存")
                }
                else -> {
                    /* Nothing to do. */
                }
            }
        }

        changeCamera.setOnClickListener {
            val status = this.vm.carverStatus.value
            if (!(status is CarverStatus.Prepared || status is CarverStatus.Finalize)) {
                toast("当前状态不能更换摄像头")
                return@setOnClickListener
            }
            gestureView.clearRect()
            vm.carver.lensFacing()
            changeCameraRotationClockwise()
        }

        lifecycleScope.launch {
            vm.carverStatus.collect { status ->
                when (status) {
                    is CarverStatus.Initial -> {
                        val b = previewView.bitmap ?: return@collect
                        PreviewViewBlurEffect(this@CarverActivity, b, blurEffect).startAnim()
                    }
                    is CarverStatus.Prepared -> {
                        gestureView.clearZoom()
                        vm.carver.zoom().observe(this@CarverActivity) {
                            it ?: return@observe
                            val z = it.zoomRatio
                            zoomText.text =
                                if (z < 10) "${String.format("%.1f", z)}X"
                                else "${round(z)}X"
                        }
                    }
                    is CarverStatus.Start -> {
                        rec.text = "REC"
                        progressBar.setProgress(MAX_RECORDING_TIME)
                        pauseAndResumeImage.setImageResource(R.drawable.ic_pause)
                        startRecBreath()
                        displayPauseAndResume()
                        controlBlockToPoint()
                        progressBarEnlarge()
                    }
                    is CarverStatus.Finalize -> {
                        val path = status.info.path
                        if (path != null) {
                            intent.putExtra("video_file", path)
                            setResult(RESULT_OK, intent)
                            saveVideoToMediaStore(File(path))
                            toast("保存路径:$path")
                        } else {
                            setResult(RESULT_CANCELED)
                            toast("保存错误")
                        }
                        timeText.text = ""
                        stopRecBreath()
                        hidePauseAndResume()
                        controlPointToBlock()
                        progressBarNarrow()
                        saving = false
                    }
                    is CarverStatus.Pause -> {
                        pauseAndResumeImage.setImageResource(R.drawable.ic_resume)
                        stopRecBreath()
                    }
                    is CarverStatus.Resume -> {
                        rec.text = "REC"
                        pauseAndResumeImage.setImageResource(R.drawable.ic_pause)
                        startRecBreath()
                    }
                    is CarverStatus.Shutdown -> {
                    }
                    is CarverStatus.Error -> {
                        Log.e(TAG, "[ERROR]", status.t)
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            vm.recordingStatus.collect { status ->
                status ?: return@collect
                status.info.let { time ->
                    timeText.text = formatRecordingTime(time)
                    progressBar.setProgress(MAX_RECORDING_TIME - time)
                    if (time >= MAX_RECORDING_TIME) {
                        vm.carver.stop()
                    }
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
        if (::vm.isInitialized) {
            vm.carver.shutdown()
        }
    }

    @Suppress("DEPRECATION")
    private fun saveVideoToMediaStore(video: File) {
        val time = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.TITLE, video.name)
            put(MediaStore.Video.Media.DISPLAY_NAME, video.name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATE_TAKEN, time)
            put(MediaStore.Video.Media.DATE_MODIFIED, time)
            put(MediaStore.Video.Media.DATE_ADDED, time)
            put(MediaStore.Video.Media.DATA, video.absolutePath)
            put(MediaStore.Video.Media.SIZE, video.length())
        }
        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
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
            interpolator = null
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
            interpolator = null
            duration = 100L
        }.start()
    }

    private fun displayPauseAndResume() {
        if (vm.impl.value == CAMERAX_CORE_IMPL) return
        ObjectAnimator.ofFloat(
            pauseAndResume,
            "alpha",
            pauseAndResume.alpha,
            1f
        ).apply {
            setAutoCancel(true)
            interpolator = null
            start()
        }
    }

    private fun hidePauseAndResume() {
        if (vm.impl.value == CAMERAX_CORE_IMPL) return
        ObjectAnimator.ofFloat(
            pauseAndResume,
            "alpha",
            pauseAndResume.alpha,
            0f
        ).apply {
            setAutoCancel(true)
            interpolator = null
            start()
        }
    }

    private fun controlBlockToPoint() {
        ObjectAnimator.ofFloat(
            this,
            "ControlPointScale",
            controlPoint.scaleX,
            0.6f
        ).apply {
            setAutoCancel(true)
            interpolator = AnticipateOvershootInterpolator()
            start()
        }
    }

    private fun controlPointToBlock() {
        ObjectAnimator.ofFloat(
            this,
            "ControlPointScale",
            controlPoint.scaleX,
            1f
        ).apply {
            setAutoCancel(true)
            interpolator = OvershootInterpolator()
            start()
        }
    }

    private fun progressBarEnlarge() {
        ObjectAnimator.ofFloat(
            this,
            "ProgressBarScale",
            progressBar.scaleX,
            1f
        ).apply {
            setAutoCancel(true)
            interpolator = OvershootInterpolator()
            start()
        }
    }

    private fun progressBarNarrow() {
        ObjectAnimator.ofFloat(
            this,
            "ProgressBarScale",
            progressBar.scaleX,
            0.8f
        ).apply {
            setAutoCancel(true)
            interpolator = AnticipateOvershootInterpolator()
            start()
        }
    }

    @Suppress("UNUSED")
    private fun setControlPointScale(scale: Float) {
        controlPoint.scaleX = scale
        controlPoint.scaleY = scale
    }

    @Suppress("UNUSED")
    private fun setProgressBarScale(scale: Float) {
        progressBar.scaleX = scale
        progressBar.scaleY = scale
    }

    private fun changeCameraRotationClockwise() {
        val r = changeCamera.rotation
        val target = r + 180 - r % 180
        ObjectAnimator.ofFloat(
            changeCamera,
            "Rotation",
            r,
            target
        ).apply {
            setAutoCancel(true)
            duration = 600L
            start()
        }
    }

}