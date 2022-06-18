package com.kokomi.carver.core.camerax.core

import android.net.Uri
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kokomi.carver.core.CarverStatus
import com.kokomi.carver.checkMainThread
import com.kokomi.carver.core.camerax.CameraXCaptor
import com.kokomi.carver.core.camerax.CameraXConfiguration
import com.kokomi.carver.core.camerax.supportedQualityList
import com.kokomi.carver.defaultOutputDirectory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock
import com.kokomi.carver.core.camerax.video.CameraXVideoCaptorImpl

/**
 * CameraX Core 包下的 [VideoCapture] 的视频录制实现
 *
 * <p>
 *
 * 可以实现视频录制开始、停止和输出文件功能，同时可以支持调节很多参数，
 * 美中不足的是，无法暂停和继续录制
 *
 * <p>
 *
 * 如果希望能够暂停和继续录制，可以使用
 * [CameraXVideoCaptorImpl]
 *
 * @see CameraXVideoCaptorImpl
 * */
@Suppress("RestrictedApi", "MissingPermission")
class CameraXCoreCaptorImpl(
    private val activity: ComponentActivity,
    override var config: CameraXConfiguration =
        CameraXConfiguration(
            outputDirectory = activity.defaultOutputDirectory()
        )
) : CameraXCaptor() {

    companion object {
        private const val TAG = "CameraXCoreCaptorImpl"
    }

    override var previewView: PreviewView? = null

    override var cameraProvider: ProcessCameraProvider? = null

    override var camera: Camera? = null

    private lateinit var videoCapture: VideoCapture

    private val prepareLock = ReentrantLock()

    private var job: Job? = null

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
                delay(100L)
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
                    // 获取视频捕获者，配置参数
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

}