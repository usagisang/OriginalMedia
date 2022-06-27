package com.kokomi.origin.entity

import android.os.Parcel
import android.os.Parcelable

internal const val TYPE_IMAGE = 1
internal const val TYPE_VIDEO = 2

data class News(
    internal val title: String,
    internal val resource: String,
    internal val content: String,
    internal val userId: Long,
    internal val uploadTime: String,
    internal val type: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun describeContents() = 1

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(resource)
        parcel.writeString(content)
        parcel.writeLong(userId)
        parcel.writeString(uploadTime)
        parcel.writeInt(type)
    }

    companion object CREATOR : Parcelable.Creator<News> {
        override fun createFromParcel(parcel: Parcel): News {
            return News(parcel)
        }

        override fun newArray(size: Int): Array<News?> {
            return arrayOfNulls(size)
        }
    }

}
