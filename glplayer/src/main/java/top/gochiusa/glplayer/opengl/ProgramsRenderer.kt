package top.gochiusa.glplayer.opengl

import android.graphics.SurfaceTexture
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import top.gochiusa.glplayer.opengl.objects.EntireScreen
import top.gochiusa.glplayer.opengl.programs.VideoShaderProgram
import top.gochiusa.glplayer.util.ShaderHelper
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ProgramsRenderer(
    private val glSurfaceView: VideoGLSurfaceView
): GLSurfaceView.Renderer {

    /**
     * OES纹理的id
     */
    private var textureId: Int = -1

    private lateinit var surfaceTexture: SurfaceTexture

    /**
     * 着色器代码实体
     */
    private lateinit var videoShaderProgram: VideoShaderProgram

    /**
     * 绘制数据实体
     */
    private lateinit var entireScreen: EntireScreen

    private var matrix: FloatArray = FloatArray(16)

    private var frameAvailable: AtomicBoolean = AtomicBoolean(false)

    // TODO 获取Video宽高
    private var videoWidth: Int = -1
    private var videoHeight: Int = -1

    private var surfaceWidth: Int = -1
    private var surfaceHeight: Int = -1

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glSurfaceView.onSurfaceTextureAvailable(init())

        Matrix.setIdentityM(matrix, 0)
        videoShaderProgram = VideoShaderProgram(glSurfaceView.context)
        entireScreen = EntireScreen()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        surfaceWidth = width
        surfaceHeight = height
        refreshMatrix()
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        if (frameAvailable.compareAndSet(true, false)) {
            surfaceTexture.updateTexImage()
        }

        videoShaderProgram.useProgram()
        videoShaderProgram.setUniforms(matrix, textureId)

        entireScreen.bindData(videoShaderProgram)
        entireScreen.draw()
    }

    private fun init(): SurfaceTexture {
        textureId = ShaderHelper.createOESTextureId()

        surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture.setOnFrameAvailableListener {
            frameAvailable.set(true)
        }
        return surfaceTexture
    }

    private fun refreshMatrix() {
        if (surfaceWidth < 0 || surfaceHeight < 0 || videoWidth < 0 || videoHeight < 0) {
            return
        }
        val screenRatio: Float = (surfaceWidth.toFloat()) / (surfaceHeight.toFloat())

        val videoRatio: Float = if (videoWidth == 0 || videoHeight == 0) {
            screenRatio
        } else {
            (videoWidth.toFloat()) / (videoHeight.toFloat())
        }

        if (videoRatio > screenRatio) {
            val r = videoRatio / screenRatio
            Matrix.orthoM(matrix, 0, -1F, 1F, -r, r, -1F, 1F)
        } else {
            val r = screenRatio / videoRatio
            Matrix.orthoM(matrix, 0, -r, r, -1F, 1F, -1F, 1F)
        }
    }
}