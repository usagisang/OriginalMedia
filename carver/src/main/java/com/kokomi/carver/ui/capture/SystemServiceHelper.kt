package com.kokomi.carver.ui.capture

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.math.abs

val carverPermissions = arrayOf(
    android.Manifest.permission.CAMERA,
    android.Manifest.permission.RECORD_AUDIO
)

fun ComponentActivity.requestCarverPermissions(listener: (Map<String, Boolean>) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        listener(result)
    }.launch(carverPermissions)
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
