package com.kokomi.uploader

import com.kokomi.uploader.entity.ReleaseInfo
import com.kokomi.uploader.listener.UploaderListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal suspend fun createImage(
    releaseInfo: ReleaseInfo,
    listener: UploaderListener
) = flow {
    var param: Any? = null
    // 获取上传图片的责任链
    val chains = imageChains(listener)
    for (i in chains.indices) {
        val chain = chains[i]
        // 调用责任链依次完成请求
        chain.initReleaseInfo(releaseInfo)
        param = chain.request(param)
    }
    // 发布成功后发布的数据重新发射回去
    emit(releaseInfo)
}.flowOn(Dispatchers.IO)

internal suspend fun createVideo(
    releaseInfo: ReleaseInfo,
    listener: UploaderListener
) = flow {
    var param: Any? = null
    // 获取上传视频的责任链
    val chains = videoChains(listener)
    for (i in chains.indices) {
        val chain = chains[i]
        // 调用责任链依次完成请求
        chain.initReleaseInfo(releaseInfo)
        param = chain.request(param)
    }
    // 发布成功后发布的数据重新发射回去
    emit(releaseInfo)
}.flowOn(Dispatchers.IO)
