package com.trio.app.data.firebase

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object FirebaseAuthManager {
    private lateinit var auth: FirebaseAuth

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    fun init(app: Application) {
        auth = Firebase.auth
        _currentUser.value = auth.currentUser
        _isLoggedIn.value = auth.currentUser != null
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun getUserId(): String? = auth.currentUser?.uid
}