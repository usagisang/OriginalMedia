package top.gochiusa.originalmedia.account.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import top.gochiusa.originalmedia.account.bean.User

interface LoginService {
    @GET("login")
    fun login(@Query("username") username:String,@Query("password") password:String):Call<User>
}