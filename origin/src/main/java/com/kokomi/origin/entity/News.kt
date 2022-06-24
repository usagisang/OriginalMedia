package com.kokomi.origin.entity

internal const val TYPE_IMAGE = 1
internal const val TYPE_VIDEO = 2

data class News(
    internal val title: String,
    internal val resource: String,
    internal val content: String,
    internal val userId: Long,
    internal val uploadTime: String,
    internal val type: Int
)
