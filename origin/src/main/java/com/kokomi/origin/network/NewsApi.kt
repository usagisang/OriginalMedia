package com.kokomi.origin.network

import com.kokomi.origin.entity.News
import com.kokomi.origin.entity.*
import com.kokomi.origin.entity.User
import com.kokomi.origin.util.default
import com.kokomi.origin.util.io
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject

object NewsApi {

    suspend fun imageNews(page: Int, limit: Int = 5) = flow<Pair<Boolean, List<News>>> {
        val body = io { apiService.imageNews(page, limit).string() }
        val json = JSONObject(body)
        val code = json.getInt("code")
        if (code != 1) throw RuntimeException("Un Know Error.")
        val results = json.getJSONArray("result")
        val list = mutableListOf<News>()
        for (i in 0 until results.length()) {
            list.add(formatImageNews(results.getJSONObject(i)))
        }
        val hasNext = json.getBoolean("hasNext")
        emit(Pair(hasNext, list))
    }.flowOn(default)

    suspend fun videoNews(page: Int, limit: Int = 5) = flow<Pair<Boolean, List<News>>> {
        val body = io { apiService.videoNews(page, limit).string() }
        val json = JSONObject(body)
        val code = json.getInt("code")
        if (code != 1) throw RuntimeException("Un Know Error.")
        val results = json.getJSONArray("result")
        val list = mutableListOf<News>()
        for (i in 0 until results.length()) {
            list.add(formatVideoNews(results.getJSONObject(i)))
        }
        val hasNext = json.getBoolean("hasNext")
        emit(Pair(hasNext, list))
    }.flowOn(default)

    suspend fun mixNews(limit: Int = 5) = flow<Pair<Boolean, List<News>>> {
        val body = io { apiService.mixNews(limit).string() }
        val json = JSONObject(body)
        val code = json.getInt("code")
        if (code != 1) throw RuntimeException("Un Know Error.")
        val results = json.getJSONArray("result")
        val list = mutableListOf<News>()
        for (i in 0 until results.length()) {
            val result = results.getJSONObject(i)
            val news = when (result.getInt("type")) {
                TYPE_IMAGE -> {
                    formatImageNews(result)
                }
                TYPE_VIDEO -> {
                    formatVideoNews(result)
                }
                else -> {
                    throw RuntimeException("Un Know Error.")
                }
            }
            list.add(news)
        }
        emit(Pair(true, list))
    }.flowOn(default)

    suspend fun login(userName: String, password: String) = flow {
        val body = io { apiService.login(userName, password).string() }
        val json = JSONObject(body)
        val code = json.getInt("code")
        if (code != 1) throw RuntimeException("Un Know Error.")
        val user = User(
            json.getString("username"),
            json.getLong("userId"),
            json.getString("nickname")
        )
        emit(user)
    }.flowOn(default)

    private fun formatImageNews(result: JSONObject) =
        News(
            result.getString("title"),
            result.getString("images"),
            result.getString("content"),
            result.getLong("userId"),
            result.getString("uploadTime"),
            TYPE_IMAGE
        )

    private fun formatVideoNews(result: JSONObject) =
        News(
            result.getString("title"),
            result.getString("videoUrl"),
            "",
            result.getLong("userId"),
            result.getString("uploadTime"),
            TYPE_VIDEO
        )

}