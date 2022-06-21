package top.gochiusa.originalmedia.base

import androidx.databinding.ViewDataBinding


interface BaseBinding<VB : ViewDataBinding> {
    fun VB.initBinding()

}
