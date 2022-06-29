package com.kokomi.uploader

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.math.abs
import kotlin.random.Random

internal fun fileKey(userId: Long, extension: String): String {
    val str = "${userId}${System.currentTimeMillis()}${abs(Random.nextInt())}"
    if (extension.isBlank()) return str.encryptMD5() ?: str
    return "${str.encryptMD5() ?: str}.$extension"
}

private fun String.encryptMD5(): String? {
    var hashedPwd: String? = null
    try {
        val md = MessageDigest.getInstance("MD5")
        md.update(toByteArray())
        hashedPwd = BigInteger(1, md.digest()).toString(16)
    } catch (ignore: NoSuchAlgorithmException) {
    }
    return hashedPwd
}