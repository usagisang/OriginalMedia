package top.gochiusa.glplayer.opengl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Surface

class VideoGLSurfaceView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): GLSurfaceView(context, attrs) {

    /**
     * 可以监听Surface相关事件的Listener
     */
    interface VideoSurfaceListener {
        /**
         * 当[surface]被创建并与[VideoGLSurfaceView]相关联时，该函数被回调
         */
        fun onVideoSurfaceCreated(surface: Surface)

        /**
         * 当[surface]与[VideoGLSurfaceView]不再关联并被释放前，该函数被回调
         */
        fun onVideoSurfaceDestroyed(surface: Surface)
    }

    private val mainHandler: Handler = Handler(Looper.myLooper()!!)

    private var surfaceTexture: SurfaceTexture? = null

    var surface: Surface? = null
        private set

    var onVideoSurfaceListener: VideoSurfaceListener? = null

    private val renderer: ProgramsRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = ProgramsRenderer(this)
        setRenderer(renderer)
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

    fun onSurfaceTextureAvailable(newSurfaceTexture: SurfaceTexture) {
        mainHandler.post {
            val oldSurfaceTexture = surfaceTexture
            val oldSurface = surface

            val newSurface = Surface(newSurfaceTexture)

            surface = newSurface
            surfaceTexture = newSurfaceTexture

            onVideoSurfaceListener?.onVideoSurfaceCreated(newSurface)

            releaseSurface(oldSurfaceTexture, oldSurface)
        }
    }

    private fun releaseSurface(surfaceTexture: SurfaceTexture?, surface: Surface?) {
        surfaceTexture?.release()
        surface?.release()
    }
}