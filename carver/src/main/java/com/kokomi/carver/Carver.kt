package com.kokomi.carver

import android.app.Application
import android.content.Context
import android.util.Log

class Carver private constructor() {

    companion object {
        private const val TAG = "Carver"

        @Suppress("StaticFieldLeak")
        private lateinit var mContext: Context

        private val INSTANCE by lazy { Carver() }

        /**
         * 初始化 [Carver]
         * */
        fun init(application: Application) {
            mContext = application.applicationContext
        }

        /**
         * 获取 [Carver] 单例
         * */
        fun get() = INSTANCE
    }

    private val mRecorder = MediaRecorderImpl()

    private val mCapture = CameraXCaptureImpl()

    private val mListeners = mutableListOf<CarverListener>()

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