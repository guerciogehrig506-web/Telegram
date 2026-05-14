package com.trio.app.data

import android.app.Application
import com.trio.app.data.api.ApiClient
import com.trio.app.data.local.TokenManager
import com.trio.app.data.model.User
import com.trio.app.data.repository.ApiResult
import com.trio.app.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SessionManager {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun init(application: Application) {
        TokenManager.init(application)
        ApiClient.init(application)
    }

    fun checkSession(onResult: (Boolean) -> Unit) {
        val token = TokenManager.getToken()
        if (token == null) {
            _isLoggedIn.value = false
            onResult(false)
            return
        }

        scope.launch {
            try {
                when (val result = AuthRepository.getMe()) {
                    is ApiResult.Success -> {
                        _currentUser.value = result.data
                        TokenManager.saveUserInfo(result.data.id, result.data.username)
                        _isLoggedIn.value = true
                        withContext(Dispatchers.Main) { onResult(true) }
                    }
                    is ApiResult.Error -> {
                        TokenManager.clear()
                        _isLoggedIn.value = false
                        withContext(Dispatchers.Main) { onResult(false) }
                    }
                    is ApiResult.Loading -> {}
                }
            } catch (e: Exception) {
                TokenManager.clear()
                _isLoggedIn.value = false
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    fun logout() {
        TokenManager.clear()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun saveToken(token: String, user: User) {
        TokenManager.saveToken(token)
        TokenManager.saveUserInfo(user.id, user.username)
        _currentUser.value = user
        _isLoggedIn.value = true
    }

    fun updateUser(user: User) {
        _currentUser.value = user
        TokenManager.saveUserInfo(user.id, user.username)
    }
}