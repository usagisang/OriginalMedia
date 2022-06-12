package com.kokomi.carver.core.camerax

import androidx.camera.core.CameraSelector
import androidx.camera.video.Quality

class CameraXConfiguration private constructor(
    internal val mLensFacing: Int,
    internal val mQuality: Quality
) {

    companion object {
        internal val supportedQualitySet = mutableSetOf<Quality>()

        fun getSupportedQualitySet(): Set<Quality> {
            return mutableSetOf<Quality>().apply {
                addAll(supportedQualitySet)
            }
        }
    }

    class Builder {

        private var mLensFacing = CameraSelector.LENS_FACING_BACK

        private var mQuality = Quality.LOWEST

        fun build() = CameraXConfiguration(
            mLensFacing, mQuality
        )

        fun setLensFacing(@CameraSelector.LensFacing lensFacing: Int) {
            mLensFacing = lensFacing
        }

        fun setQuality(quality: Quality) {
            mQuality = quality
        }

    }

}