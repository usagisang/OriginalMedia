package com.kokomi.carver.core.camerax

import android.net.Uri
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kokomi.carver.core.Captor
import com.kokomi.carver.core.CarverStatus
import com.kokomi.carver.checkMainThread
import com.kokomi.carver.defaultOutputDirectory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock

@Suppress("RestrictedApi", "MissingPermission")
class CameraXCoreCaptorImpl(
    private val activity: ComponentActivity,
    override var config: CameraXConfiguration =
        CameraXConfiguration(
            outputDirectory = activity.defaultOutputDirectory()
        )
) : Captor<PreviewView, CameraXConfiguration>() {

    companion object {
        private const val TAG = "CameraXCoreCaptorImpl"
    }

    private var previewView: PreviewView? = null

    private var cameraProvider: ProcessCameraProvider? = null

    private var camera: Camera? = null

    private lateinit var videoCapture: VideoCapture

    private val prepareLock = ReentrantLock()

    private var job: Job? = null

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
        val videoFile = config.outputFile()
        // 输出控制
        val outputOptions = VideoCapture.OutputFileOptions.Builder(videoFile).build()
        // 准备开始录制
        videoCapture.startRecording(
            outputOptions,
            ContextCompat.getMainExecutor(activity),
            object : VideoCapture.OnVideoSavedCallback {

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    job?.cancel()
                    changeStatus(CarverStatus.Error(cause ?: RuntimeException("未知错误")))
                }

                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    job?.cancel()
                    changeStatus(CarverStatus.Finalize(Uri.fromFile(videoFile)))
                }

            }
        )
        val startTime = SystemClock.uptimeMillis()
        job = activity.lifecycleScope.launch {
            changeStatus(CarverStatus.Start())
            while (true) {
                changeStatus(CarverStatus.Recording(1000_000 * (SystemClock.uptimeMillis() - startTime)))
                delay(500L)
            }
        }
    }

    override fun stop() {
        checkMainThread()
        videoCapture.stopRecording()
    }

    override fun pause() {
        // Nothing
    }

    override fun resume() {
        // Nothing
    }

    private fun prepareInternal() {
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
                    videoCapture = VideoCapture.Builder().apply {
                        with(config) {
                            if (videoFrameRate > 0) setVideoFrameRate(videoFrameRate)
                            if (bitRate > 0) setBitRate(bitRate)
                            if (iFrameInterval > 0) setIFrameInterval(iFrameInterval)
                            if (audioSampleRate > 0) setAudioSampleRate(audioSampleRate)
                            if (audioBitRate > 0) setAudioBitRate(audioBitRate)
                            if (audioChannelCount > 0) setAudioChannelCount(audioChannelCount)
                        }
                    }.build()
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

    override fun changeLensFacing() {
        val facing = if (config.lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        config = config.copy(lensFacing = facing)
        onConfigurationChanged(config)
    }

    override fun zoom(zoom: Float) {
        camera?.run {
            cameraControl.setLinearZoom(zoom)
        }
    }

}