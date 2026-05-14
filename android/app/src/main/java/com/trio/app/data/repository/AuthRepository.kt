package com.trio.app.data.repository

import com.trio.app.data.api.ApiClient
import com.trio.app.data.api.LoginRequest
import com.trio.app.data.api.RegisterRequest
import com.trio.app.data.local.TokenManager
import com.trio.app.data.model.LoginResponse
import com.trio.app.data.model.RegisterResponse
import com.trio.app.data.model.User

object AuthRepository {
    suspend fun register(username: String, email: String, password: String): ApiResult<RegisterResponse> {
        return try {
            val response = ApiClient.apiService.register(RegisterRequest(username, email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    TokenManager.saveToken(body.token)
                    TokenManager.saveUserInfo(body.user.id, body.user.username)
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error("注册失败：服务器返回空数据")
                }
            } else {
                ApiResult.Error("注册失败，该邮箱可能已被注册")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "网络连接失败")
        }
    }

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        return try {
            val response = ApiClient.apiService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    TokenManager.saveToken(body.token)
                    TokenManager.saveUserInfo(body.user.id, body.user.username)
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error("登录失败：服务器返回空数据")
                }
            } else {
                ApiResult.Error("登录失败，请检查邮箱和密码")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "网络连接失败")
        }
    }

    suspend fun getMe(): ApiResult<User> {
        return try {
            val response = ApiClient.apiService.getMe()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error("服务器返回空数据")
                }
            } else {
                ApiResult.Error("Token 失效")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "网络连接失败")
        }
    }
}