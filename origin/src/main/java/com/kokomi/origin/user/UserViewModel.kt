package com.kokomi.origin.user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kokomi.origin.base.loggedUser
import com.kokomi.origin.datastore.*
import com.kokomi.origin.entity.User
import com.kokomi.origin.network.NewsApi
import com.kokomi.origin.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

private val EMPTY_USER = User("", -1, "")

class UserViewModel : ViewModel() {

    internal val userName = MutableStateFlow("")

    internal val password = MutableStateFlow("")

    private val _user = MutableStateFlow(EMPTY_USER)
    internal val user: StateFlow<User> = _user

    private val _isLogged = MutableStateFlow(false)
    internal val isLogged: StateFlow<Boolean> = _isLogged

    internal fun login(context: Context) {
        viewModelScope.launch {
            NewsApi.login(userName.value, password.value)
                .catch {
                    it.printStackTrace()
                    _user emit EMPTY_USER
                    loggedUser = null
                    _isLogged emit false
                    if (it is SocketTimeoutException) {
                        toastNetworkError()
                    } else {
                        toast("登陆失败")
                    }
                }.collect { user ->
                    context saveUser user
                    _user emit user
                    loggedUser = user
                    _isLogged emit true
                }
        }
    }

    internal fun logout(context: Context) {
        viewModelScope.launch {
            userName emit ""
            password emit ""
            _user emit EMPTY_USER
            context.clearUser()
            loggedUser = null
            _isLogged emit false
        }
    }

    internal fun loadUser() {
        viewModelScope.launch {
            val user = loggedUser
            if (user == null) {
                _user emit EMPTY_USER
                _isLogged emit false
            } else {
                _user emit user
                _isLogged emit true
            }
        }
    }

}