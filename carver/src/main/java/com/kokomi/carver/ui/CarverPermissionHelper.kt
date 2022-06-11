package com.kokomi.carver.ui

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

val carverPermissions = arrayOf(
    android.Manifest.permission.CAMERA,
    android.Manifest.permission.RECORD_AUDIO
)

fun ComponentActivity.requestCarverPermissions(listener: (Map<String, Boolean>) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        listener(result)
    }.launch(carverPermissions)
}
