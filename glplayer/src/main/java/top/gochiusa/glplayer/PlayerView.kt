package top.gochiusa.glplayer

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import top.gochiusa.glplayer.base.Player
import top.gochiusa.glplayer.opengl.VideoGLSurfaceView
import top.gochiusa.glplayer.util.Assert

/**
 * 播放器渲染媒体的顶层视图
 */
class PlayerView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context) {

    private var internalPlayer: Player? = null

    private val surfaceView: SurfaceView

    init {
        surfaceView = VideoGLSurfaceView(context)
        addView(surfaceView, 0,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    fun setPlayer(player: Player?) {
        Assert.verifyMainThread("PlayerView:setPlayer is accessed on the wrong thread")

        if (internalPlayer != player) {
            val oldPlayer = internalPlayer
            oldPlayer?.clearVideoSurfaceView(surfaceView)
            internalPlayer = player
            player?.setVideoSurfaceView(surfaceView)
        }
    }


    /**
     *  当用户可以看见播放平面时应当调用的方法。此方法与[onResume]相对应
     */
    fun onResume() {
        if (surfaceView is GLSurfaceView) {
            surfaceView.onResume()
        }
    }

    /**
     * 当用户不再能够看见播放平面时应当调用的方法。此方法与[onResume]相对应
     */
    fun onPause() {
        if (surfaceView is GLSurfaceView) {
            surfaceView.onPause()
        }
    }

    override fun onDetachedFromWindow() {
        internalPlayer?.clearVideoSurfaceView(surfaceView)
        super.onDetachedFromWindow()
    }
}