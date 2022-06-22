package com.kokomi.origin.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment : Fragment() {

    protected inline fun <reified VM : ViewModel> viewModel(bind: VM.() -> Unit) {
        ViewModelProvider(this)[VM::class.java].bind()
    }

    protected inline fun <VM : ViewModel> viewModel(vm: Class<VM>, bind: VM.() -> Unit) {
        ViewModelProvider(this)[vm].bind()
    }

    protected inline fun <reified VM : ViewModel> activityViewModel(bind: VM.() -> Unit) {
        ViewModelProvider(requireActivity())[VM::class.java].bind()
    }

}