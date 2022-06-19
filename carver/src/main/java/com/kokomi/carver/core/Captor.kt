package com.kokomi.carver.core

import androidx.lifecycle.LiveData

/**
 * 捕获者抽象类，提供实现录制模块的接口
 *
 * @param P 预览视图的类型
 * @param C 配置的类型
 * @param Z 返回缩放倍数的类型
 * */
abstract class Captor<P, C, Z> {

    /**
     * 参数配置
     * */
    protected abstract var config: C

    private lateinit var carver: Carver<P, C, Z>

    internal fun attachTo(carver: Carver<P, C, Z>) {
        this.carver = carver
    }

    /**
     * 调用此方法通知绑定的 [Carver] 状态更新
     * */
    protected fun changeStatus(status: CarverStatus) {
        carver.onStatusChanged(status)
    }

    internal fun getConfig() = config

    /**
     * 结束当前捕获者
     * */
    abstract fun shutdown()

    /**
     * 配置更新
     * */
    abstract fun onConfigurationChanged(newConfig: C)

    /**
     * 绑定预览视图
     * */
    abstract fun bindPreview(preview: P)

    /**
     * 录制前的准备
     * */
    abstract fun prepare()

    /**
     * 录制开始
     * */
    abstract fun start()

    /**
     * 录制结束
     * */
    abstract fun stop()

    /**
     * 录制停止
     * */
    abstract fun pause()

    /**
     * 录制继续
     * */
    abstract fun resume()

    /**
     * 改变镜头方向
     * */
    abstract fun changeLensFacing()

    /**
     * 获取存有变焦比信息的 [LiveData] 对象
     * */
    abstract fun zoom(): LiveData<Z>

    /**
     * 调节缩放比例, 范围是 0 至 1 闭区间
     * */
    abstract fun zoom(zoom: Float)

    /**
     * 聚焦
     * */
    abstract fun focus(x: Float, y: Float)

    /**
     * 取消聚焦
     * */
    abstract fun cancelFocus()

}