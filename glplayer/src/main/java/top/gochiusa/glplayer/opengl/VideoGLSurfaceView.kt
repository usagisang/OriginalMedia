package top.gochiusa.glplayer.opengl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Surface
import top.gochiusa.glplayer.base.SurfaceProvider
import top.gochiusa.glplayer.entity.Format
import top.gochiusa.glplayer.listener.VideoMetadataListener
import top.gochiusa.glplayer.listener.VideoSurfaceListener

class VideoGLSurfaceView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    private var onVideoSurfaceListener: VideoSurfaceListener? = null
): GLSurfaceView(context, attrs), VideoMetadataListener, SurfaceProvider,
    SurfaceTexture.OnFrameAvailableListener {

    private val mainHandler: Handler = Handler(Looper.myLooper()!!)

    private var surfaceTexture: SurfaceTexture? = null

    override var surface: Surface? = null
        private set

    override fun setOnVideoSurfaceListener(listener: VideoSurfaceListener?) {
        onVideoSurfaceListener = listener
    }

    private val renderer: ProgramsRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = ProgramsRenderer(this)
        setRenderer(renderer)
        // 设置为DIRTY时才渲染
        renderMode = RENDERMODE_WHEN_DIRTY
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mainHandler.post {
            val oldSurface = surface
            oldSurface?.let {
                onVideoSurfaceListener?.onVideoSurfaceDestroyed(it)
            }
            releaseSurface(surfaceTexture, oldSurface)
            surfaceTexture = null
            surface = null
        }
    }

    internal fun onSurfaceTextureAvailable(newSurfaceTexture: SurfaceTexture) {
        mainHandler.post {
            val oldSurfaceTexture = surfaceTexture
            val oldSurface = surface

            val newSurface = Surface(newSurfaceTexture)

            surface = newSurface
            surfaceTexture = newSurfaceTexture

            newSurfaceTexture.setOnFrameAvailableListener(this)

            onVideoSurfaceListener?.onVideoSurfaceCreated(newSurface)

            releaseSurface(oldSurfaceTexture, oldSurface)
        }
    }

    private fun releaseSurface(surfaceTexture: SurfaceTexture?, surface: Surface?) {
        surfaceTexture?.let {
            it.setOnFrameAvailableListener(null)
            it.release()
        }
        surface?.release()
    }

    override fun onVideoMetadataChanged(format: Format) {
        renderer.setVideoInfo(format.width, format.height, format.rotation)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        queueEvent {
            surfaceTexture?.updateTexImage()
            if (surfaceTexture != null) {
                requestRender()
            }
        }
    }
}