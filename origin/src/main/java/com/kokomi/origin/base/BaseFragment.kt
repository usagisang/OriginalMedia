package com.kokomi.origin.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment : Fragment() {

    protected inline fun <reified VM : ViewModel> viewModel(bind: VM.() -> Unit) {
        ViewModelProvider(this)[VM::class.java].bind()
    }

}