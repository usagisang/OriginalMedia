package com.kokomi.carver.core

import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File

@Suppress("RestrictedApi", "MissingPermission")
class CameraXCaptorImpl(activity: ComponentActivity) : Captor<PreviewView>() {

    private val mActivity = activity

    private lateinit var mConfig: RecorderConfiguration

    private lateinit var mPreviewView: PreviewView

    private var mVideoCapture: VideoCapture<Recorder>? = null

    private lateinit var mRecording: Recording

    override fun shutdown() {
        // Nothing to do.
        // Because VideoCapture binds to activity.
        // Their life cycle is consistent.
    }

    override fun configure(configuration: RecorderConfiguration) {
        checkMainThread()
        mConfig = configuration
        preparePrivate()
    }

    override fun setPreview(preview: PreviewView) {
        checkMainThread()
        mPreviewView = preview
    }

    override fun prepare() {
        checkMainThread()
        preparePrivate()
    }

    override fun reset() {
        checkMainThread()
    }

    override fun start() {
        checkMainThread()
        val videoFile = File(
            outputDirectory,
            "${System.currentTimeMillis()}.mp4"
        )
        val outputOptions = FileOutputOptions.Builder(videoFile).build()
        mRecording = mVideoCapture!!.output
            .prepareRecording(mActivity, outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(mActivity)) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> {}
                    is VideoRecordEvent.Finalize -> {}
                    is VideoRecordEvent.Status -> {
                        val stats = event.recordingStats
                        println(stats.recordedDurationNanos)
                    }
                    is VideoRecordEvent.Pause -> {}
                    is VideoRecordEvent.Resume -> {}
                }
            }
    }

    override fun stop() {
        checkMainThread()
        mRecording.stop()
    }

    override fun pause() {
        checkMainThread()
        mRecording.pause()
    }

    override fun resume() {
        checkMainThread()
        mRecording.resume()
    }

    private lateinit var outputDirectory: File

    private fun preparePrivate() {
        mVideoCapture = VideoCapture.withOutput(
            Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
        )
        val cameraProviderFuture = ProcessCameraProvider.getInstance(mActivity)
        outputDirectory = getOutputDirectory()
        cameraProviderFuture.addListener(
            {
                val preview = Preview.Builder().build()
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                val cameraProvider = cameraProviderFuture.get()!!
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    mActivity, cameraSelector, preview, mVideoCapture
                )
                preview.setSurfaceProvider(mPreviewView.surfaceProvider)
            }, ContextCompat.getMainExecutor(mActivity)
        )
    }

    private fun getOutputDirectory(): File {
        val mediaDir = mActivity.externalMediaDirs.firstOrNull()?.let {
            File(it, mActivity.packageName).apply { mkdir() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else mActivity.filesDir
    }

}