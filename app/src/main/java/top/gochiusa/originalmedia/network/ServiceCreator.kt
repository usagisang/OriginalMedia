package top.gochiusa.originalmedia.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
<<<<<<< HEAD
    private const val BASE_URL = "8.136.115.103:8080/"
=======
    private const val BASE_URL = "http://8.136.115.103:8080/NewsApi/"
>>>>>>> 639667ce55b5d3351307e62f3c5ca269332e3307
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)
}