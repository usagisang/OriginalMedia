package com.kokomi.carver.core.camerax

import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.kokomi.carver.core.Captor
import com.kokomi.carver.core.CarverStatus
import com.kokomi.carver.core.checkMainThread
import java.io.File
import java.util.concurrent.locks.ReentrantLock

@Suppress("RestrictedApi", "MissingPermission")
class CameraXCaptorImpl(
    private val activity: ComponentActivity,
    private var config: CameraXConfiguration? = null
) : Captor<PreviewView, CameraXConfiguration>() {

    private var previewView: PreviewView? = null

    private var cameraProvider: ProcessCameraProvider? = null

    private lateinit var videoCapture: VideoCapture<Recorder>

    private lateinit var recording: Recording

    private val outputDirectory = getOutputDirectory()

    private val prepareLock = ReentrantLock()

    override fun shutdown() {
        checkMainThread()
        // 解绑所有绑定的使用案例，然后销毁
        cameraProvider?.unbindAll()
        cameraProvider?.shutdown()
    }

    override fun onConfigurationChanged(newConfig: CameraXConfiguration) {
        checkMainThread()
        // 更新配置，然后重新构建录制者
        config = newConfig
        prepareInternal()
    }

    override fun bindPreview(preview: PreviewView) {
        checkMainThread()
        previewView = preview
    }

    override fun prepare() {
        checkMainThread()
        prepareInternal()
    }

    override fun start() {
        checkMainThread()
        // 保存路径
        val videoFile = File(
            outputDirectory,
            "${System.currentTimeMillis()}.mp4"
        )
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
                        changeStatus(CarverStatus.Stop())
                    }
                    is VideoRecordEvent.Status -> {
                        // 返回信息中包含录制信息
                        changeStatus(CarverStatus.Recording(event.recordingStats))
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

    private fun prepareInternal() {
        prepareLock.lock()
        changeStatus(CarverStatus.Initial())
        cameraProvider?.unbindAll()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener(
            {
                try {
                    val preview = Preview.Builder().build()
                    // 获取相机选择器
                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(
                            config?.mLensFacing ?: CameraSelector.LENS_FACING_BACK
                        )
                        .build()
                    // 获取视频捕获者
                    videoCapture = VideoCapture.withOutput(
                        Recorder.Builder()
                            .setQualitySelector(
                                QualitySelector.from(
                                    config?.mQuality ?: Quality.LOWEST
                                )
                            )
                            .build()
                    )
                    // 通过相机提供者获取相机实例，并把相机的信息绑定到预览视图和视频捕获者
                    cameraProvider = cameraProviderFuture.get()
                    val camera = cameraProvider!!.bindToLifecycle(
                        activity, cameraSelector, preview, videoCapture
                    )
                    // 将相机支持的分辨率记录到配置类中
                    CameraXConfiguration.supportedQualitySet.apply {
                        clear()
                        addAll(QualitySelector.getSupportedQualities(camera.cameraInfo))
                    }
                    // 设置预览界面
                    previewView?.run {
                        preview.setSurfaceProvider(surfaceProvider)
                    }
                    changeStatus(CarverStatus.Prepared())
                } catch (t: Throwable) {
                    changeStatus(CarverStatus.Error(t))
                } finally {
                    prepareLock.unlock()
                }
            }, ContextCompat.getMainExecutor(activity)
        )
    }

    private fun getOutputDirectory(): File {
        val mediaDir = activity.externalMediaDirs.firstOrNull()?.let {
            File(it, activity.packageName).apply { mkdir() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else activity.filesDir
    }

}