package top.gochiusa.glplayer.mediacodec

import android.os.Handler
import top.gochiusa.glplayer.base.Renderer
import top.gochiusa.glplayer.base.RendererFactory
import top.gochiusa.glplayer.listener.VideoMetadataListener

class CodecRendererFactory: RendererFactory {

    override fun createRenders(
        eventHandler: Handler,
        metadataListener: VideoMetadataListener
    ): Array<Renderer> {
        return arrayOf(
            MediaCodecAudioRenderer(),
            MediaCodecVideoRenderer(videoMetadataListener = metadataListener),
        )
    }
}