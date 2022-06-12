package com.kokomi.carver.core

/**
 * 捕获者抽象类，提供实现录制模块的接口
 *
 * @param P 预览视图的类型
 * @param C 配置的类型
 * */
abstract class Captor<P, C> {

    private lateinit var mCarver: Carver<P, C>

    internal fun attachTo(carver: Carver<P, C>) {
        mCarver = carver
    }

    /**
     * 调用此方法通知绑定的 [Carver] 状态更新
     * */
    protected fun changeStatus(status: CarverStatus) {
        mCarver.onStatusChanged(status)
    }

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

}