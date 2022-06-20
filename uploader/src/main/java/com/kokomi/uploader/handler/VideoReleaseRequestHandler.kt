package com.kokomi.uploader.handler

import com.kokomi.uploader.UploaderException
import com.kokomi.uploader.network.releaseService
import okhttp3.ResponseBody

class VideoReleaseRequestHandler : RequestHandler() {

    override suspend fun request(any: Any?): ResponseBody {
        return releaseService.video(
            releaseInfo.userId.toString(),
            any?.toString() ?: throw UploaderException("Failed to get video URL."),
            releaseInfo.title
        )
    }

}