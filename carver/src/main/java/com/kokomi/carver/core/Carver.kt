package com.kokomi.carver.core

import android.util.Log

private const val TAG = "Carver"

class Carver<P>(captor: Captor<P>) {

    private var mCaptor = captor

    private val mListeners = mutableListOf<CarverListener>()

    fun shutdown() = mCaptor.shutdown()

    /**
     * 更新录制的配置
     *
     * @param configuration 要更新的配置
     * */
    fun configure(configuration: RecorderConfiguration) = mCaptor.configure(configuration)

    fun reset() = mCaptor.reset()

    fun setPreview(preview: P) = mCaptor.setPreview(preview)

    fun prepare() = mCaptor.prepare()

    fun start() = mCaptor.start()

    fun pause() = mCaptor.pause()

    fun resume() = mCaptor.resume()

    /**
     * 注册一个尚未存在的监听者，请确保该监听者失去作用时进行注销，否则可能造成不必要的内存泄漏
     *
     * @param listener 要注册的监听者
     * @see Carver.unregisterListener
     * */
    fun registerListener(listener: CarverListener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener)
        } else {
            Log.e(TAG, "The listener already exists.")
        }
    }

    /**
     * 注销一个存在的监听者
     *
     * @param listener 要注销的监听者
     * @see Carver.registerListener
     * */
    fun unregisterListener(listener: CarverListener) {
        mListeners.indexOf(listener).let {
            if (it >= 0) {
                mListeners.removeAt(it)
            } else {
                Log.e(TAG, "The listener does not exists.")
            }
        }
    }

}