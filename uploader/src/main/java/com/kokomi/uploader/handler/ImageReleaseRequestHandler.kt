package com.kokomi.uploader.handler

import com.kokomi.uploader.UploaderException
import com.kokomi.uploader.network.releaseService
import okhttp3.ResponseBody

class ImageReleaseRequestHandler : RequestHandler() {

    override suspend fun request(any: Any?): ResponseBody {
        return releaseService.image(
            releaseInfo.userId,
            releaseInfo.title,
            any?.toString() ?: throw UploaderException("Failed to get picture URL."),
            releaseInfo.content
        )
    }

}