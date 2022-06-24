package com.kokomi.origin.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kokomi.origin.entity.User
import com.kokomi.origin.util.io
import kotlinx.coroutines.flow.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user")

private val userName by lazy { stringPreferencesKey("userName") }
private val userId by lazy { longPreferencesKey("userId") }
private val nickName by lazy { stringPreferencesKey("nickName") }

suspend infix fun Context.saveUser(user: User) {
    io {
        dataStore.edit {
            it[userName] = user.userName
            it[userId] = user.userId
            it[nickName] = user.nickName
        }
    }
}

suspend fun Context.loadUser(): User? {
    return io {
        var user: User? = null
        dataStore.data.first {
            val userId = it[userId] ?: -1
            if (userId == -1L) return@first true
            val userName = it[userName] ?: ""
            val nickName = it[nickName] ?: ""
            user = User(userName, userId, nickName)
            true
        }
        user
    }
}

suspend fun Context.clearUser() {
    io { dataStore.edit { it.clear() } }
}
