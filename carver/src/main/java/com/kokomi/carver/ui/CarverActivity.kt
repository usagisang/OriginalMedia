package com.kokomi.carver.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import com.kokomi.carver.R
import com.kokomi.carver.core.CameraXCaptorImpl
import com.kokomi.carver.core.Carver
import com.kokomi.carver.core.clearSystemWindows
import com.kokomi.carver.core.setStatusBarTextColor

class CarverActivity : AppCompatActivity() {

    private val mCaptor = CameraXCaptorImpl(this)
    private lateinit var mCarver: Carver<PreviewView>

    private lateinit var statusBar: TextView
    private lateinit var timeText: TextView
    private lateinit var previewView: PreviewView
    private lateinit var control: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carver)

        statusBar = findViewById(R.id.tv_carver_status_bar)

        requestCarverPermissions {
            it.map { result ->
                if (result.key == carverPermissions[0]) {
                    if (!result.value) {
                        Toast.makeText(this, "未授予拍照权限，无法录制视频", Toast.LENGTH_LONG).show()
                        finish()
                        return@map
                    }
                } else if (result.key == carverPermissions[1]) {
                    if (!result.value) {
                        Toast.makeText(this, "未授予录音权限，无法录制音频", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        init()
                    }
                }
            }
        }
    }

    private fun init() {
        timeText = findViewById(R.id.tv_carver_time)
        previewView = findViewById(R.id.pv_carver_preview)
        control = findViewById(R.id.cv_carver_control)

        mCarver = Carver(mCaptor)

        mCaptor.setPreview(previewView)
        mCaptor.prepare()
        mCaptor.start()
    }

    override fun onResume() {
        super.onResume()
        clearSystemWindows(statusBar)
        setStatusBarTextColor(true)
    }

}