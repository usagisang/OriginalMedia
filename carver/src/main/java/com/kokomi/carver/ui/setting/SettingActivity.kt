package com.kokomi.carver.ui.setting

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.kokomi.carver.setStatusBarTextColor

class SettingActivity : ComponentActivity() {

    private lateinit var vm: SettingViewModel

    private var initResolvingPower: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[SettingViewModel::class.java]
        vm.impl.value = intent.getStringExtra("impl")!!
        initResolvingPower = vm.supportedQuality.indexOf(intent.getStringExtra("resolving_power"))
        vm.selectedResolvingPower.value = initResolvingPower
        setContent {
            CarverTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SoftBlack
                ) {
                    SettingContentView(vm = vm)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.statusBarColor = Color.rgb(0x21, 0x21, 0x21)
        setStatusBarTextColor(true)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishInternal()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    private fun finishInternal() {
        val selectedResolvingPower = vm.selectedResolvingPower.value
        if (selectedResolvingPower != initResolvingPower) {
            intent.putExtra("resolving_power", selectedResolvingPower)
            setResult(RESULT_OK, intent)
        } else {
            setResult(RESULT_CANCELED, intent)
        }
        finish()
    }

}

