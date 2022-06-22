package com.kokomi.origin.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kokomi.origin.appContext
import com.kokomi.origin.datastore.UserDataStore.loadUser
import com.kokomi.origin.datastore.UserDataStore.saveUser
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

    internal fun loadUser() {
        viewModelScope.launch {
            appContext.loadUser().collect { user ->
                _user.emit(user)
            }
        }
    }

    internal fun login() {
        viewModelScope.launch {
            NewsApi.login(userName.value, password.value)
                .catch {
                    it.printStackTrace()
                    _user.emit(EMPTY_USER)
                }
                .collect { user ->
                    _user.emit(user)
                    appContext.saveUser(user)
                }
        }
    }

    internal fun logout() {
        viewModelScope.launch {
            userName.value = ""
            password.value = ""
            _user.value = EMPTY_USER
            appContext.saveUser(EMPTY_USER)
        }
    }

}