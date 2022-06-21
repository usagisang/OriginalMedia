package top.gochiusa.originalmedia.video.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import top.gochiusa.originalmedia.video.data.bean.VideoResultJson

interface VideoService {

    @GET("content/video")
    fun getVideos(@Query("page") page: Int, @Query("limit") size: Int): Call<VideoResultJson>
}