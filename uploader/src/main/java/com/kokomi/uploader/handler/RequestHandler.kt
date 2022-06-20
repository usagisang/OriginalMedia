package com.kokomi.uploader.handler

import com.kokomi.uploader.entity.ReleaseInfo

abstract class RequestHandler {

    protected lateinit var releaseInfo: ReleaseInfo

    internal fun initReleaseInfo(releaseInfo: ReleaseInfo) {
        this.releaseInfo = releaseInfo
    }

    abstract suspend fun request(any: Any?): Any?

}