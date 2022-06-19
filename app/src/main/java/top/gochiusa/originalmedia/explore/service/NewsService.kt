package top.gochiusa.originalmedia.explore.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import top.gochiusa.originalmedia.explore.bean.GraphicResponse
import top.gochiusa.originalmedia.util.Constant

interface NewsService {
    @GET("api/news/list?app_id=${Constant.APP_ID}&app_secret=${Constant.APP_SECRET}")
    fun graphicList(@Query("typeId")  typeId:String,@Query("page") page:String): Call<GraphicResponse>
}