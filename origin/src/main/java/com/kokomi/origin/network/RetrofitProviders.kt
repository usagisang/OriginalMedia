package com.kokomi.origin.network

import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://8.136.115.103:8080/NewsApi/"
private const val IMAGE_NEWS = "content/article"
private const val VIDEO_NEWS = "content/video"
private const val MIX_NEWS = "content/random"
private const val LOGIN = "login"

private val apiRetrofit by lazy { Retrofit.Builder().baseUrl(BASE_URL).build() }

internal val apiService by lazy { apiRetrofit.create<Api>() }

internal interface Api {

    @GET(IMAGE_NEWS)
    suspend fun imageNews(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ResponseBody

    @GET(VIDEO_NEWS)
    suspend fun videoNews(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ResponseBody

    @GET(MIX_NEWS)
    suspend fun mixNews(
        @Query("limit") limit: Int
    ): ResponseBody

    @GET(LOGIN)
    suspend fun login(
        @Query("username") userName: String,
        @Query("password") password: String
    ): ResponseBody

}