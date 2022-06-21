package top.gochiusa.originalmedia.explore.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import top.gochiusa.originalmedia.explore.bean.Graphic
import top.gochiusa.originalmedia.explore.bean.GraphicResponse
import top.gochiusa.originalmedia.explore.bean.GraphicResult
import top.gochiusa.originalmedia.util.Constant

interface NewsService {
    @GET("content/article")
    fun graphicList(@Query("page")  page:Int, @Query("limit") limit:Int): Call<GraphicResult>
}