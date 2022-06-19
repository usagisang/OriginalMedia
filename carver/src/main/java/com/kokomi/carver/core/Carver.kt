package com.kokomi.carver.core

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData

/**
 * 使用 [Carver] ，可以进行视频录制
 *
 * <p>
 *
 * [Carver] 是包装 [Captor] 的类，它不涉及任何录制视频的核心操作，
 * 使用 [Carver] 可以更加方便地对 [Captor] 进行状态监听，
 * 同时也可以调节 [Captor] 的参数
 *
 * @param captor 录制视频的核心类
 * @param listener [Captor] 状态的监听者
 * */
class Carver<P, C, Z>(
    private val captor: Captor<P, C, Z>,
    private val listener: (CarverStatus) -> Unit
) {

    init {
        captor.attachTo(this)
    }

    /**
     * 调用此函数以终结 [Captor]
     * */
    fun shutdown() {
        captor.shutdown()
    }

    /**
     * 调用此函数来令 [Captor] 接收新的参数配置
     *
     * @param config 新的参数配置
     * */
    fun config(config: C) {
        captor.onConfigurationChanged(config)
    }

    /**
     * 调用此函数获取 [Captor] 当前的参数配置
     *
     * @return [Captor] 当前的参数配置
     * */
    fun config(): C {
        return captor.getConfig()
    }

    /**
     * 绑定一个预览界面
     *
     * @param preview 预览界面对象
     * */
    fun bindPreview(preview: P) {
        captor.bindPreview(preview)
    }

    /**
     * 当 [Captor] 已经配置好参数并设置好预览对象后，调用此函数可以准备进行录制，
     * 并为用户提供相机预览界面
     * */
    fun prepare() {
        captor.prepare()
    }

    /**
     * 开始录制，此函数在 [prepare] 函数调用并且 [Captor] 准备完毕之后调用
     * */
    fun start() {
        captor.start()
    }

    /**
     * 停止录制，并保存文件
     * */
    fun stop() {
        captor.stop()
    }

    /**
     * 暂停录制，暂停时可以使用 [stop] 函数停止录制或 [resume] 函数继续录制
     * */
    fun pause() {
        captor.pause()
    }

    /**
     * 继续录制，此函数必须在 [Captor] 为暂停录制状态时才可以调用
     * */
    fun resume() {
        captor.resume()
    }

    /**
     * 更换相机方向，若当前为后置摄像头，则改为前置，若当前为前置摄像头，则改为后置
     * */
    fun lensFacing() {
        captor.changeLensFacing()
    }

    /**
     * 获取变焦比的 [LiveData] 对象
     *
     * @return 变焦比的 [LiveData] 对象
     * */
    fun zoom(): LiveData<Z> {
        return captor.zoom()
    }

    /**
     * 调节变焦比，数值从 0 ~ 1 闭区间，0 表示最小变焦倍数，
     * 而 1 则表示最大变焦倍数
     * */
    fun zoom(zoom: Float) {
        captor.zoom(zoom)
    }

    /**
     * 令摄像头聚焦于某一点
     *
     * @param x 该点的 x 坐标
     * @param y 该点的 y 坐标
     * */
    fun focus(x: Float, y: Float) {
        captor.focus(x, y)
    }

    /**
     * 取消聚焦
     * */
    fun cancelFocus() {
        captor.cancelFocus()
    }

    internal fun onStatusChanged(status: CarverStatus) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            listener(status)
        } else {
            Handler(Looper.getMainLooper()).post {
                listener(status)
            }
        }
    }

}