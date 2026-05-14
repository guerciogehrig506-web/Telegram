package com.trio.app.data.local

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "trio_secure_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveToken(token: String) {
        prefs?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }

    fun getToken(): String? {
        return prefs?.getString(KEY_TOKEN, null)
    }

    fun saveUserInfo(userId: String, username: String) {
        prefs?.edit()
            ?.putString(KEY_USER_ID, userId)
            ?.putString(KEY_USERNAME, username)
            ?.apply()
    }

    fun getUserId(): String? = prefs?.getString(KEY_USER_ID, null)
    fun getUsername(): String? = prefs?.getString(KEY_USERNAME, null)

    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }
}