package com.calendar.core.data.repository

import com.calendar.core.network.TokenManager
import com.calendar.core.network.api.AuthApi
import com.calendar.core.network.dto.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 认证Repository实现
 * 注：网络功能暂时禁用，仅保留本地Token管理
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager
) {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    /**
     * 用户登录（本地模拟）
     */
    suspend fun login(email: String, password: String): Result<UserDTO> {
        // 简化实现：直接保存token
        tokenManager.saveTokens(
            accessToken = "local_token",
            refreshToken = "local_refresh",
            expiresIn = 86400000L // 24小时
        )
        tokenManager.saveUserInfo(1L, email)
        _isLoggedIn.value = true
        return Result.success(UserDTO(id = 1L, email = email, username = email))
    }

    /**
     * 用户注册（本地模拟）
     */
    suspend fun register(email: String, password: String, username: String?): Result<UserDTO> {
        return login(email, password)
    }

    /**
     * 登出
     */
    suspend fun logout() {
        tokenManager.clearToken()
        _isLoggedIn.value = false
    }

    /**
     * 检查是否已登录
     */
    fun checkLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    /**
     * 获取当前用户信息
     */
    suspend fun getCurrentUser(): Result<UserDTO> {
        val email = tokenManager.getUserEmail() ?: return Result.failure(Exception("Not logged in"))
        val userId = tokenManager.getUserId()
        return Result.success(UserDTO(id = userId, email = email, username = email))
    }
}
