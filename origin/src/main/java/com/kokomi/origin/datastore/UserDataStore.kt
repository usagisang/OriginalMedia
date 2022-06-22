package com.kokomi.origin.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kokomi.origin.entity.User
import com.kokomi.origin.io
import kotlinx.coroutines.flow.*

object UserDataStore {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user")

    private val userName by lazy { stringPreferencesKey("userName") }
    private val userId by lazy { longPreferencesKey("userId") }
    private val nickName by lazy { stringPreferencesKey("nickName") }

    suspend fun Context.saveUser(user: User) {
        io {
            dataStore.edit {
                it[userName] = user.userName
                it[userId] = user.userId
                it[nickName] = user.nickName
            }
        }
    }

    suspend fun Context.loadUser() = flow {
        dataStore.data.catch { }.collect {
            emit(
                User(
                    it[userName] ?: "",
                    it[userId] ?: -1,
                    it[nickName] ?: ""
                )
            )
        }
    }.flowOn(io)

}