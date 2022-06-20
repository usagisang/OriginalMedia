package top.gochiusa.originalmedia.video.data.bean

import top.gochiusa.originalmedia.video.entity.Video

class VideoResultJson(
    val code: Int,
    val result: List<Video>,
    val hasNext: Boolean
)