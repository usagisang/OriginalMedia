package com.kokomi.uploader.listener

interface UploaderListener {

    /**
     * 上传状态监听
     *
     * @param name 上传文件名
     * @param progress 上传进度，范围是 0 ~ 100 闭区间
     * */
    fun onUploading(name: String, progress: Int)

}