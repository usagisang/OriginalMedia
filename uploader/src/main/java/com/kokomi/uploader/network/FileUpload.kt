package com.kokomi.uploader.network

import com.qiniu.android.common.FixedZone
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager

internal const val QI_NIU_HOST = "http://rdomwxl7m.hn-bkt.clouddn.com/"

val uploadQiNiu by lazy {
    UploadManager(
        Configuration.Builder()
            .connectTimeout(90)
            .useHttps(true)
            .resumeUploadVersion(Configuration.RESUME_UPLOAD_VERSION_V2)
            .useConcurrentResumeUpload(true)
            .concurrentTaskCount(3)
            .responseTimeout(90)
//            .recorder(recorder) // recorder分片上传时，已上传片记录器。默认null
//            .recorder(recorder, keyGen) // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
            .zone(FixedZone.zone2)
            .build()
    )
}

