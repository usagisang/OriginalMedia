package top.gochiusa.originalmedia.video.data.net

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 通过该单例类，可以获取Retrofit接口.
 * */
internal object ServiceCreator {
    private const val BASE_URL = "http://8.136.115.103:8080/NewsApi/"

    private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit = builder.build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
}