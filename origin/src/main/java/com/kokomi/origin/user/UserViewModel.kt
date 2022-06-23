package com.kokomi.origin.user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kokomi.origin.datastore.*
import com.kokomi.origin.entity.User
import com.kokomi.origin.network.NewsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

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
                    _user.emit(EMPTY_USER)
                    _isLogged.emit(false)
                }.collect { user ->
                    context.saveUser(user)
                    _user.emit(user)
                    _isLogged.emit(true)
                }
        }
    }

    internal fun logout(context: Context) {
        viewModelScope.launch {
            userName.emit("")
            password.emit("")
            _user.emit(EMPTY_USER)
            context.clearUser()
            _isLogged.emit(false)
        }
    }

    internal fun Context.loadUserFromDataStore() {
        viewModelScope.launch {
            val user = loadUser()
            if (user != null) {
                _user.emit(user)
                _isLogged.emit(true)
            } else {
                _user.emit(EMPTY_USER)
                _isLogged.emit(false)
            }
        }
    }

}