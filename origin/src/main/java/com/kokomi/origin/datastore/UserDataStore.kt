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
import kotlinx.coroutines.runBlocking

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

suspend fun Context.loadUser(): Flow<User?> =
    flow<User?> {
        dataStore.data.first {
            val userId = it[userId] ?: -1L
            if (userId == -1L) {
                emit(null)
                return@first true
            }
            val userName = it[userName] ?: ""
            val nickName = it[nickName] ?: ""
            emit(User(userName, userId, nickName))
            true
        }
    }.flowOn(io)

suspend fun Context.clearUser() {
    io { dataStore.edit { it.clear() } }
}
