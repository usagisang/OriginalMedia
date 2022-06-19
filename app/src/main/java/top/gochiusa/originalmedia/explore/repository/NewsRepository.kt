package top.gochiusa.originalmedia.explore.repository

import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import top.gochiusa.originalmedia.network.NewsNetWork

object NewsRepository {
    fun graphicList(typeId: String, page: String) = liveData(Dispatchers.IO) {
        val result = try {

            val graphicResponse = NewsNetWork.graphicList(typeId, page)

            if (graphicResponse.code == 1) {

                val graphicList = graphicResponse.data
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




