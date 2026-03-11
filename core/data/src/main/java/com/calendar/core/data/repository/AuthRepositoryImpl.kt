package com.calendar.core.data.repository

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 认证Repository实现（本地模式）
 * 注：网络功能暂时禁用，仅保留本地Token管理
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
    }

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    /**
     * 用户登录（本地模拟）
     */
    suspend fun login(email: String, password: String): Result<UserInfo> {
        // 简化实现：直接保存token
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, "local_token")
            .putString(KEY_REFRESH_TOKEN, "local_refresh")
            .putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + 86400000L)
            .putLong(KEY_USER_ID, 1L)
            .putString(KEY_USER_EMAIL, email)
            .apply()
        _isLoggedIn.value = true
        return Result.success(UserInfo(id = 1L, email = email, username = email))
    }

    /**
     * 用户注册（本地模拟）
     */
    suspend fun register(email: String, password: String, username: String?): Result<UserInfo> {
        return login(email, password)
    }

    /**
     * 登出
     */
    suspend fun logout() {
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_TOKEN_EXPIRY)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_EMAIL)
            .apply()
        _isLoggedIn.value = false
    }

    /**
     * 检查是否已登录
     */
    fun checkLoggedIn(): Boolean {
        val token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        val expiry = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)
        return token != null && System.currentTimeMillis() < expiry
    }

    /**
     * 获取当前用户信息
     */
    suspend fun getCurrentUser(): Result<UserInfo> {
        val email = sharedPreferences.getString(KEY_USER_EMAIL, null) 
            ?: return Result.failure(Exception("Not logged in"))
        val userId = sharedPreferences.getLong(KEY_USER_ID, -1)
        return Result.success(UserInfo(id = userId, email = email, username = email))
    }
}

/**
 * 用户信息
 */
data class UserInfo(
    val id: Long,
    val email: String,
    val username: String?
)
