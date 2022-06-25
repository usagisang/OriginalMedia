package com.kokomi.uploader.handler

import com.kokomi.uploader.UploaderException
import com.kokomi.uploader.fileKey
import com.kokomi.uploader.listener.UploaderListener
import com.kokomi.uploader.network.QI_NIU_HOST
import com.kokomi.uploader.network.uploadQiNiu
import com.qiniu.android.http.ResponseInfo
import com.qiniu.android.storage.UploadOptions
import kotlin.math.round

class DataUploadRequestHandler(
    private val listener: UploaderListener
) : RequestHandler() {

    override suspend fun request(any: Any?): String {
        val file = releaseInfo.file
        val inputStreamInfo = releaseInfo.inputStreamInfo
        return if (file != null)
            uploadQiNiu.syncPut(
                file,
                fileKey(releaseInfo.userId, file.extension),
                any?.toString() ?: throw UploaderException("Failed to get upload token."),
                UploadOptions(null, null, false,
                    { key, percent -> listener.onUploading(key, round(100 * percent).toInt()) },
                    { false }
                )).resourceUrl()
        else {
            val name = fileKey(releaseInfo.userId, inputStreamInfo!!.second)
            uploadQiNiu.syncPut(
                inputStreamInfo.first,
                null,
                -1,
                name,
                name,
                any?.toString() ?: throw UploaderException("Failed to get upload token."),
                UploadOptions(null, null, false,
                    { key, percent -> listener.onUploading(key, round(100 * percent).toInt()) },
                    { false }
                )).resourceUrl()
        }
    }

    private fun ResponseInfo.resourceUrl(): String {
        if (isOK) {
            return "$QI_NIU_HOST${response.get("key")}"
        } else {
            throw UploaderException(error)
        }
    }

}