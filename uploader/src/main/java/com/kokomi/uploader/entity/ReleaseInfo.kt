package com.kokomi.uploader.entity

import java.io.File
import java.io.InputStream

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
     * 文件，图片或视频文件，优先使用 [file]
     * */
    val file: File? = null,
    /**
     * 图片或视频的输入流，优先使用 [file]，第二个参数是文件后缀名，不带 "."，
     * 例如：Pair(inputStream, jpg)
     * */
    val inputStreamInfo: Pair<InputStream, String>? = null
)
