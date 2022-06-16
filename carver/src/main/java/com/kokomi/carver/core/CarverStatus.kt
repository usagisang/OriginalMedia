package com.kokomi.carver.core

import android.net.Uri

sealed class CarverStatus(code: Int) {

    class Initial : CarverStatus(0)

    class Prepared : CarverStatus(1)

    class Start : CarverStatus(2)

    class Finalize(val info: Uri) : CarverStatus(4)

    class Recording(val info: Long) : CarverStatus(8)

    class Pause : CarverStatus(16)

    class Resume : CarverStatus(32)

    class Shutdown : CarverStatus(64)

    class Error(val t: Throwable) : CarverStatus(128)

}
