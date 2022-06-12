package com.kokomi.carver.core

sealed class CarverStatus(code: Int) {

    class Initial : CarverStatus(0)

    class Prepared : CarverStatus(1)

    class Start : CarverStatus(2)

    class Stop : CarverStatus(4)

    class Recording<I>(val info: I) : CarverStatus(8)

    class Pause : CarverStatus(16)

    class Resume : CarverStatus(32)

    class Shutdown : CarverStatus(64)

    class Error(t: Throwable) : CarverStatus(128)

}

//@Target(AnnotationTarget.VALUE_PARAMETER)
//@Retention(AnnotationRetention.SOURCE)
//@IntDef(value = [Status.Initial, Status.Prepare, Status.Start, Status.Stop, Status.Pause, Status.Resume, Status.Shutdown])
//annotation class CarverStatus
//
//interface Status {
//
//    companion object {
//
//        const val Initial = 0
//
//        const val Prepare = 1
//
//        const val Start = 2
//
//        const val Stop = 4
//
//        const val Pause = 8
//
//        const val Resume = 16
//
//        const val Shutdown = 32
//
//    }
//
//}