package com.kokomi.carver.core.camerax

import androidx.camera.core.CameraSelector
import androidx.camera.video.Quality
import java.io.File

/**
 * 摄像头准备完毕后，这个列表中会存有当前摄像头支持的所有视频质量
 * */
internal val supportedQualityList = mutableListOf<Quality>()

/**
 * 复制一份摄像头支持的视频质量并返回
 * */
fun getSupportedQualities(): List<Quality> {
    return mutableListOf<Quality>().apply {
        addAll(supportedQualityList)
    }
}

/**
 * [CameraXCoreCaptorImpl] 和 [CameraXVideoCaptorImpl] 的参数配置类
 * */
data class CameraXConfiguration internal constructor(
    /**
     * 摄像头方向，可以传入的值有：[CameraSelector.LENS_FACING_BACK]
     * 和 [CameraSelector.LENS_FACING_FRONT]
     * */
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    /**
     * 视频质量，支持的值有：[Quality.SD] 、[Quality.HD] 、[Quality.FHD] 、[Quality.UHD]
     * */
    val quality: Quality = Quality.LOWEST,
    /**
     * 视频帧率，单位：帧每秒
     * */
    val videoFrameRate: Int = -1,
    /**
     * 视频文件码率，单位：比特每秒
     * */
    val bitRate: Int = -1,
    /**
     * I 帧间隔。单位：秒
     * */
    val iFrameInterval: Int = -1,
    /**
     * 音频采样率，单位：赫兹
     * */
    val audioSampleRate: Int = -1,
    /**
     * 音频文件比特率，单位：比特每秒
     * */
    val audioBitRate: Int = -1,
    /**
     * 音频通道数
     * */
    val audioChannelCount: Int = -1,
    /**
     * 输出文件夹
     * */
    val outputDirectory: File,
    /**
     * 输出文件名
     * */
    val outputFile: () -> File = {
        File(
            outputDirectory,
            "${System.currentTimeMillis()}.mp4"
        )
    }
)

/**
 * 用于格式化视频质量
 * */
fun qualityFormatter(quality: Quality) =
    when (quality) {
        Quality.LOWEST -> "最低"
        Quality.SD -> "SD, 480P"
        Quality.HD -> "HD, 720P"
        Quality.FHD -> "FHD, 1080P"
        Quality.UHD -> "UHD, 2160P"
        Quality.HIGHEST -> "最高"
        else -> {
            "未知分辨率"
        }
    }