package com.kokomi.uploader.handler

import com.kokomi.uploader.UploaderException
import com.kokomi.uploader.fileKey
import com.kokomi.uploader.listener.UploaderListener
import com.kokomi.uploader.network.QI_NIU_HOST
import com.kokomi.uploader.network.uploadQiNiu
import com.qiniu.android.http.ResponseInfo
import com.qiniu.android.storage.UploadOptions
import kotlin.math.round

class FileUploadRequestHandler(
    private val listener: UploaderListener
) : RequestHandler() {

    override suspend fun request(any: Any?): List<String> {
        val list = mutableListOf<String>()
        for (i in releaseInfo.files.indices) {
            val info = uploadQiNiu.syncPut(
                releaseInfo.files[i],
                fileKey(releaseInfo.userId, releaseInfo.files[i], i),
                any?.toString() ?: throw UploaderException("Failed to get upload token."),
                UploadOptions(null, null, false,
                    { key, percent -> listener.onUploading(key, round(100 * percent).toInt()) },
                    { false }
                ))
            list.add(info.resourceUrl())
        }
        return list
    }

    private fun ResponseInfo.resourceUrl(): String {
        return "$QI_NIU_HOST${response.get("key")}"
    }

}