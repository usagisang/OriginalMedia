package com.kokomi.carver.ui.capture

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File

/**
 * 录制要请求的权限列表
 * */
val carverPermissions = arrayOf(
    android.Manifest.permission.CAMERA,
    android.Manifest.permission.RECORD_AUDIO
)

/**
 * 请求权限
 * */
fun ComponentActivity.requestCarverPermissions(listener: (Map<String, Boolean>) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        listener(result)
    }.launch(carverPermissions)
}

@Suppress("DEPRECATION")
fun Activity.saveVideoToMediaStore(video: File) {
    val time = System.currentTimeMillis()
    val values = ContentValues().apply {
        put(MediaStore.Video.Media.TITLE, video.name)
        put(MediaStore.Video.Media.DISPLAY_NAME, video.name)
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        put(MediaStore.Video.Media.DATE_TAKEN, time)
        put(MediaStore.Video.Media.DATE_MODIFIED, time)
        put(MediaStore.Video.Media.DATE_ADDED, time)
        put(MediaStore.Video.Media.DATA, video.absolutePath)
        put(MediaStore.Video.Media.SIZE, video.length())
    }
    val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
    sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
}

fun Context.registerAccelerometerListener(listener: () -> Unit) {
//    val sensorManager = getSystemService(Context.SENSOR_SERVICE) as? (SensorManager) ?: return
//    val senor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: return
//    sensorManager.registerListener(object : SensorEventListener {
//        override fun onSensorChanged(event: SensorEvent) {
//            val x = abs(event.values[0])
//            val y = abs(event.values[1])
//            val z = abs(event.values[2])
//            if (x > 10 || y > 10 || z > 10) {
//                listener()
//            }
//        }
//
//        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
//        }
//    }, senor, SensorManager.SENSOR_DELAY_GAME)
}
