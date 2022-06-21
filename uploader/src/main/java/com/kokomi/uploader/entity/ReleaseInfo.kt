package com.kokomi.uploader.entity

import java.io.File

data class ReleaseInfo(
    /**
     * 用户 ID
     * */
    val userId: Long,
    /**
     * 标题
     * */
    val title: String,
    /**
     * 内容，如果是视频，[content] 字段将不会被使用
     * */
    val content: String,
    /**
     * 文件，图片或视频，图片可以多张，但是视频就不要多个了，视频一个就好
     * */
    val files: List<File>
)
