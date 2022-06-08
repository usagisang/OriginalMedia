package com.kokomi.carver

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

object CarverPermissionHelper {

    private val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO
    )

    fun ComponentActivity.requestCarverPermission(listener: (Map.Entry<String, Boolean>) -> Unit) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.map { listener }
        }.apply {
            launch(permissions)
        }
    }

}