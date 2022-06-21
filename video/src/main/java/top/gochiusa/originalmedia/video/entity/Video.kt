package top.gochiusa.originalmedia.video.entity

data class Video(
    val userId: Long,
    val videoUrl: String,
    val uploadTime: String,
    val title: String
)