package top.gochiusa.originalmedia.account.repository

import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import top.gochiusa.originalmedia.network.NewsNetWork


object AccountRepository {

    fun getLogin(username: String, password: String) = liveData(Dispatchers.IO) {
        val result = try {
            val user = NewsNetWork.login(username, password)
            if (user.code == 1) {
                Result.success(user)
            } else {
                Result.failure(RuntimeException("response status is${user.code}"))

            }

        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)

    }
}