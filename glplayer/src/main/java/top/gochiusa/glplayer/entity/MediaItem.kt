package top.gochiusa.glplayer.entity

import android.net.Uri

class MediaItem {

    internal val url: String
    internal val uri: Uri?

    private constructor(uri: Uri) {
        this.uri = uri
        url = ""
    }

    private constructor(url: String) {
        this.url = url
        uri = null
    }

    fun localSource(): Boolean = uri != null

    companion object {

        fun fromUrl(url: String): MediaItem = MediaItem(url)

        fun fromUri(uri: Uri): MediaItem = MediaItem(uri)
    }

}