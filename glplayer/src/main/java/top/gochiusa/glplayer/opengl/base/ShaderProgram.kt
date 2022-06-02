package top.gochiusa.glplayer.opengl.base

import android.content.Context
import android.opengl.GLES20.*
import androidx.annotation.RawRes
import top.gochiusa.glplayer.util.ShaderHelper
import top.gochiusa.glplayer.util.readStringFromRaw


abstract class ShaderProgram(
    context: Context,
    @RawRes vertexShaderResId: Int,
    @RawRes fragmentShaderResId: Int,
) {
    protected val programId: Int

    init {
        with(context) {
            programId = ShaderHelper.buildProgram(
                readStringFromRaw(vertexShaderResId),
                readStringFromRaw(fragmentShaderResId)
            )
        }
    }

    fun getAttribLocation(name: String): Int = glGetAttribLocation(programId, name)

    fun getUniformLocation(name: String): Int = glGetUniformLocation(programId, name)

    fun useProgram() {
        glUseProgram(programId)
    }
}