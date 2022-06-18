package com.kokomi.carver.core.camerax.video

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.kokomi.carver.core.CarverStatus
import com.kokomi.carver.checkMainThread
import com.kokomi.carver.core.camerax.CameraXCaptor
import com.kokomi.carver.core.camerax.CameraXConfiguration
import com.kokomi.carver.core.camerax.supportedQualityList
import com.kokomi.carver.defaultOutputDirectory
import java.util.concurrent.locks.ReentrantLock

@Suppress("RestrictedApi", "MissingPermission")
class CameraXVideoCaptorImpl(
    private val activity: ComponentActivity,
    override var config: CameraXConfiguration =
        CameraXConfiguration(
            quality = Quality.LOWEST,
            outputDirectory = activity.defaultOutputDirectory()
        )
) : CameraXCaptor() {

    companion object {
        private const val TAG = "CameraXVideoCaptorImpl"
    }

    override var previewView: PreviewView? = null

    override var cameraProvider: ProcessCameraProvider? = null

    override var camera: Camera? = null

    private lateinit var videoCapture: VideoCapture<Recorder>

    private lateinit var recording: Recording

    private val prepareLock = ReentrantLock()

    override fun start() {
        checkMainThread()
        // 保存路径
        val videoFile = config.outputFile()
        // 输出控制
        val outputOptions = FileOutputOptions.Builder(videoFile).build()
        // 准备开始录制
        recording = videoCapture.output
            .prepareRecording(activity, outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(activity)) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> {
                        changeStatus(CarverStatus.Start())
                    }
                    is VideoRecordEvent.Finalize -> {
                        changeStatus(CarverStatus.Finalize(event.outputResults.outputUri))
                    }
                    is VideoRecordEvent.Status -> {
                        // 返回信息中包含录制信息
                        changeStatus(CarverStatus.Recording(event.recordingStats.recordedDurationNanos))
                    }
                    is VideoRecordEvent.Pause -> {
                        changeStatus(CarverStatus.Pause())
                    }
                    is VideoRecordEvent.Resume -> {
                        changeStatus(CarverStatus.Resume())
                    }
                }
            }
    }

    override fun stop() {
        checkMainThread()
        recording.stop()
    }

    override fun pause() {
        checkMainThread()
        recording.pause()
    }

    override fun resume() {
        checkMainThread()
        recording.resume()
    }

    override fun prepareInternal() {
        prepareLock.lock()
        cameraProvider?.unbindAll()
        changeStatus(CarverStatus.Initial())
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener(
            {
                try {
                    val preview = Preview.Builder().build()
                    // 获取相机选择器
                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(config.lensFacing)
                        .build()
                    // 获取视频捕获者
                    videoCapture = VideoCapture.withOutput(
                        Recorder.Builder()
                            .setQualitySelector(
                                QualitySelector.from(config.quality)
                            )
                            .build()
                    )
                    // 通过相机提供者获取相机实例，并把相机的信息绑定到预览视图和视频捕获者
                    cameraProvider = cameraProviderFuture.get()
                    cameraProvider!!.unbindAll()
                    camera = cameraProvider!!.bindToLifecycle(
                        activity, cameraSelector, preview, videoCapture
                    ).apply {
                        // 将相机支持的分辨率记录到配置类中
                        supportedQualityList.apply {
                            clear()
                            val qualities = QualitySelector.getSupportedQualities(cameraInfo)
                            add(Quality.LOWEST)
                            for (i in qualities.size - 1 downTo 0) {
                                add(qualities[i])
                            }
                            add(Quality.HIGHEST)
                        }
                    }
                    // 设置预览界面
                    previewView?.run {
                        preview.setSurfaceProvider(surfaceProvider)
                    }
                    changeStatus(CarverStatus.Prepared())
                } catch (t: Throwable) {
                    Log.e(TAG, "[ERROR]", t)
                    changeStatus(CarverStatus.Error(t))
                } finally {
                    prepareLock.unlock()
                }
            }, ContextCompat.getMainExecutor(activity)
        )
    }

}