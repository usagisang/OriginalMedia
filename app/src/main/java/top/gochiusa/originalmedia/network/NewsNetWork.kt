package top.gochiusa.originalmedia.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
<<<<<<< HEAD
import top.gochiusa.originalmedia.account.service.LoginService
=======
// import top.gochiusa.originalmedia.account.service.LoginService
>>>>>>> 639667ce55b5d3351307e62f3c5ca269332e3307
import top.gochiusa.originalmedia.explore.service.NewsService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NewsNetWork {
    private val graphicService = ServiceCreator.create<NewsService>()
<<<<<<< HEAD
    private val loginService = ServiceCreator.create<LoginService>()
    suspend fun login(username:String,password:String) =
        loginService.login(username,password).await()
=======
    //private val loginService = ServiceCreator.create<LoginService>()
    suspend fun login(username:String,password:String) {
        // loginService.login(username,password).await()
    }

>>>>>>> 639667ce55b5d3351307e62f3c5ca269332e3307

    suspend fun graphicList(typeId:String,page:String) =
        graphicService.graphicList(typeId,page).await()


    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine { continuation ->
            enqueue(object :Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {

                    val body = response.body()
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