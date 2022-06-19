package com.kokomi.carver.core

import android.net.Uri

/**
 * [Captor] 的状态
 * */
sealed class CarverStatus(code: Int) {

    /**
     * 初始态
     * */
    class Initial : CarverStatus(0)

    /**
     * 准备完毕
     * */
    class Prepared : CarverStatus(1)

    /**
     * 开始录制的一瞬间
     * */
    class Start : CarverStatus(2)

    /**
     * 录制已完成
     *
     * @param info 录制文件保存路径 Uri
     * */
    class Finalize(val info: Uri) : CarverStatus(4)

    /**
     * 正在录制
     *
     * @param info 录制时长，单位：纳秒
     * */
    class Recording(val info: Long) : CarverStatus(8)

    /**
     * 暂停录制
     * */
    class Pause : CarverStatus(16)

    /**
     * 暂停录制后重新开始的一瞬间
     * */
    class Resume : CarverStatus(32)

    /**
     * 表示 [Captor] 已经被终结
     * */
    class Shutdown : CarverStatus(64)

    /**
     * 表示 [Captor] 出现错误
     * */
    class Error(val t: Throwable) : CarverStatus(128)

}
