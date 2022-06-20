package top.gochiusa.originalmedia.explore.repository

import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import top.gochiusa.originalmedia.network.NewsNetWork

object NewsRepository {
    fun graphicList(page: Int, limit: Int) = liveData(Dispatchers.IO) {
        val result = try {

            val graphicResponse = NewsNetWork.graphicList(page, limit)

            if (graphicResponse.code == 1) {

                val graphicList = graphicResponse.result
                Result.success(graphicList)
            } else {
                Result.failure(RuntimeException("response status is${graphicResponse.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }
}




