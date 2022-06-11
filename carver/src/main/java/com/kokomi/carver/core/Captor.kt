package com.kokomi.carver.core

abstract class Captor<P> {

    internal abstract fun shutdown()

    internal abstract fun configure(configuration: RecorderConfiguration)

    internal abstract fun setPreview(preview: P)

    internal abstract fun prepare()

    internal abstract fun reset()

    internal abstract fun start()

    internal abstract fun stop()

    internal abstract fun pause()

    internal abstract fun resume()

}