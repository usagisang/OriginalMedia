package top.gochiusa.glplayer.opengl.base

interface ProgramData<T: ShaderProgram> {

    fun bindData(program: T)

    fun draw()
}