package com.kokomi.uploader.network

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.*
import java.util.concurrent.TimeUnit

private const val RELEASE_HOST = "http://8.136.115.103:8080/NewsApi/"
private const val TOKEN = "content/token"
private const val IMAGE = "article/upload"
private const val VIDEO = "video/upload"

private val releaseRetrofit by lazy {
    Retrofit.Builder()
        .baseUrl(RELEASE_HOST)
        .client(
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()
        )
        .build()
}

internal val releaseService by lazy { releaseRetrofit.create(ReleaseApi::class.java) }

internal interface ReleaseApi {

    @GET(TOKEN)
    suspend fun token(): ResponseBody

    @FormUrlEncoded
    @POST(IMAGE)
    suspend fun image(
        @Field("userId") userId: Long,
        @Field("title") title: String,
        @Field("images") images: String,
        @Field("content") content: String
    ): ResponseBody

    @GET(VIDEO)
    suspend fun video(
        @Query("userId") userId: String,
        @Query("title") title: String,
        @Query("videoUrl") videoUrl: String
    ): ResponseBody

}