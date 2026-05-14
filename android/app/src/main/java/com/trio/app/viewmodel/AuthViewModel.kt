package com.trio.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trio.app.data.SessionManager
import com.trio.app.data.api.ApiClient
import com.trio.app.data.model.LoginResponse
import com.trio.app.data.model.RegisterResponse
import com.trio.app.data.repository.ApiResult
import com.trio.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<ApiResult<LoginResponse>?>(null)
    val loginState: StateFlow<ApiResult<LoginResponse>?> = _loginState

    private val _registerState = MutableStateFlow<ApiResult<RegisterResponse>?>(null)
    val registerState: StateFlow<ApiResult<RegisterResponse>?> = _registerState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = ApiResult.Loading
            val result = AuthRepository.login(email, password)
            _loginState.value = result
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = ApiResult.Loading
            val result = AuthRepository.register(username, email, password)
            _registerState.value = result
        }
    }

    fun clearLoginState() { _loginState.value = null }
    fun clearRegisterState() { _registerState.value = null }
}