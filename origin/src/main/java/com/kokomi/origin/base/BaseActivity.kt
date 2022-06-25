package com.kokomi.origin.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity : AppCompatActivity() {

    protected inline fun <reified VM : ViewModel> viewModel(bind: VM.() -> Unit) {
        ViewModelProvider(this)[VM::class.java].bind()
    }

}