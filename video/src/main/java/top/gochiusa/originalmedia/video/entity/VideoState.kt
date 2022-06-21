package top.gochiusa.originalmedia.video.entity

sealed class VideoState {
    object Loading: VideoState()
    object Error: VideoState()
    object Playing: VideoState()
    object Pause: VideoState()
}