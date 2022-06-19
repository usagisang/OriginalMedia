package top.gochiusa.originalmedia.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import top.gochiusa.originalmedia.explore.service.NewsService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NewsNetWork {
    private val graphicService = ServiceCreator.create<NewsService>()

    suspend fun graphicList(typeId:String,page:String) =
        graphicService.graphicList(typeId,page).await()


    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine { continuation ->
            enqueue(object :Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {

                    val body = response.body()
                    println("我看看$body   ")
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