package com.kokomi.uploader

import com.kokomi.uploader.handler.*
import com.kokomi.uploader.listener.UploaderListener

// 上传图片责任链
internal fun imageChains(listener: UploaderListener): List<RequestHandler> {
    return listOf(
        TokenRequestHandler(),
        DataUploadRequestHandler(listener),
        ImageReleaseRequestHandler()
    )
}

// 上传视频责任链
internal fun videoChains(listener: UploaderListener): List<RequestHandler> {
    return listOf(
        TokenRequestHandler(),
        DataUploadRequestHandler(listener),
        VideoReleaseRequestHandler()
    )
}