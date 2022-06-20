package top.gochiusa.originalmedia.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import top.gochiusa.originalmedia.account.service.LoginService
import top.gochiusa.originalmedia.explore.service.NewsService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NewsNetWork {
    private val graphicService = ServiceCreator.create<NewsService>()
    private val loginService = ServiceCreator.create<LoginService>()
    suspend fun login(username:String,password:String) =
        loginService.login(username,password).await()



    suspend fun graphicList(page:Int,limit:Int) =
        graphicService.graphicList(page,limit).await()


    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine { continuation ->
            enqueue(object :Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {

                    val body = response.body()
                    println("想看看$body")
                    if (body!=null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)                }

            })
        }
    }


}