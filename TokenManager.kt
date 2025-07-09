package com.example.authapp.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore("auth_prefs")
object TokenManager {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }
    suspend fun getToken(context: Context): String? {
        return context.dataStore.data.first()[TOKEN_KEY]
    }
    suspend fun clearToken(context: Context) {
        context.dataStore.edit { prefs -> prefs.remove(TOKEN_KEY) }
    }
}