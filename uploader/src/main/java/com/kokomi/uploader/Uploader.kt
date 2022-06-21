package com.kokomi.uploader

import com.kokomi.uploader.entity.ReleaseInfo
import com.kokomi.uploader.listener.UploaderListener

object Uploader {

    /**
     * 上传图片
     *
     * @param releaseInfo 上传图片的信息
     * @param listener 上传图片的进度监听
     *
     * @return 一个冷流，调用 collect 函数即可开始上传，上传过程中会在 [listener] 中回调上传的进度，
     * 当上传完成时，会将传入的 [releaseInfo] 发射回去告知已经上传成功，若要捕获异常，请调用 catch 函数
     * */
    suspend fun uploadImage(releaseInfo: ReleaseInfo, listener: UploaderListener) =
        createImage(releaseInfo, listener)

    /**
     * 上传视频
     *
     * @param releaseInfo 上传视频的信息
     * @param listener 上传图片的进度监听
     *
     * @return 一个冷流，调用 collect 函数即可开始上传，上传过程中会在 [listener] 中回调上传的进度，
     * 当上传完成时，会将传入的 [releaseInfo] 发射回去告知已经上传成功，若要捕获异常，请调用 catch 函数
     * */
    suspend fun uploadVideo(releaseInfo: ReleaseInfo, listener: UploaderListener) =
        createVideo(releaseInfo, listener)


}