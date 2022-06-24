package com.kokomi.origin.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal val main = Dispatchers.Main

internal val io = Dispatchers.IO

internal val default = Dispatchers.Default

internal suspend inline fun <T> io(noinline block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.IO, block)
}

internal suspend inline fun <T> main(noinline block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Main, block)
}

internal suspend inline fun <T> default(noinline block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Default, block)
}