package com.kokomi.carver.core.camerax

import androidx.camera.core.CameraSelector
import androidx.camera.video.Quality
import java.io.File

internal val supportedQualitySet = mutableSetOf<Quality>()

fun getSupportedQualities(): Set<Quality> {
    return mutableSetOf<Quality>().apply {
        addAll(supportedQualitySet)
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
