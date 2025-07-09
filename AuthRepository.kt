package com.example.authapp.repository

import android.content.Context
import com.example.authapp.model.LoginRequest
import com.example.authapp.model.UserCreate
import com.example.authapp.model.UserOut
import com.example.authapp.remote.AuthApi
import com.example.authapp.util.TokenManager

class AuthRepository(private val api: AuthApi, private val context: Context) {
    suspend fun register(email: String, password: String): Result<UserOut> {
        val response = api.register(UserCreate(email, password))
        return if (response.isSuccessful) Result.success(response.body()!!) else
            Result.failure(Exception(response.errorBody()?.string()))
    }
    suspend fun login(email: String, password: String): Result<String> {
        val response = api.login(LoginRequest(email, password))
        return if (response.isSuccessful) {
            val token = response.body()!!.access_token
            TokenManager.saveToken(context, token)
            Result.success(token)
        }
        else {
            Result.failure(Exception("Usuário ou senha inválidos"))
        }
    }
    suspend fun me(): Result<UserOut> {
        val token = TokenManager.getToken(context) ?: return Result.failure(Exception("Sem token"))
        val response = api.me("Bearer $token")
        return if (response.isSuccessful) Result.success(response.body()!!) else Result.failure(Exception("Token inválido"))
    }
    suspend fun logout() {
        TokenManager.clearToken(context)
    }
}