package com.kokomi.origin.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kokomi.origin.entity.User
import com.kokomi.origin.network.NewsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    internal val userName = MutableStateFlow("")

    internal val password = MutableStateFlow("")

    private val _user = MutableStateFlow<User?>(null)
    internal val user: StateFlow<User?> = _user

    internal fun login() {
        viewModelScope.launch {
            NewsApi.login(userName.value, password.value)
                .catch { it.printStackTrace() }
                .collect { user ->
                    _user.emit(user)
                }
        }
    }

}