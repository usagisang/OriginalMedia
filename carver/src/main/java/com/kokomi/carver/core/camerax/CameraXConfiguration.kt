package com.kokomi.carver.core.camerax

import androidx.camera.core.CameraSelector
import androidx.camera.video.Quality
import java.io.File

internal val supportedQualityList = mutableListOf<Quality>()

fun getSupportedQualities(): List<Quality> {
    return mutableListOf<Quality>().apply {
        addAll(supportedQualityList)
    }
}

data class CameraXConfiguration internal constructor(
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    val quality: Quality = Quality.LOWEST,
    val outputDirectory: File,
    val outputFile: () -> File = {
        File(
            outputDirectory,
            "${System.currentTimeMillis()}.mp4"
        )
    }
)

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