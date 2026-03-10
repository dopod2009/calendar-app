package com.calendar.core.data.repository

import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.domain.repository.EventRepository
import com.calendar.core.network.TokenManager
import com.calendar.core.network.api.AuthApi
import com.calendar.core.network.api.EventApi
import com.calendar.core.network.dto.*
import com.calendar.core.data.model.EventEntity
import com.calendar.core.data.model.EventDao
import com.calendar.core.data.model.toEntity
import com.calendar.core.data.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 认证Repository实现
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    /**
     * 用户注册
     */
    suspend fun register(email: String, password: String, username: String?): Result<UserDTO> {
        return try {
            val request = RegisterRequest(
                email = email,
                password = password,
                username = username
            )
            
            val response = authApi.register(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()?.data!!
                tokenManager.saveTokens(
                    accessToken = authResponse.token,
                    refreshToken = authResponse.refreshToken,
                    expiresIn = authResponse.expiresIn
                )
                tokenManager.saveUserInfo(authResponse.user.id, authResponse.user.email)
                Result.success(authResponse.user)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 用户登录
     */
    suspend fun login(email: String, password: String): Result<UserDTO> {
        return try {
            val request = LoginRequest(
                email = email,
                password = password
            )
            
            val response = authApi.login(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()?.data!!
                tokenManager.saveTokens(
                    accessToken = authResponse.token,
                    refreshToken = authResponse.refreshToken,
                    expiresIn = authResponse.expiresIn
                )
                tokenManager.saveUserInfo(authResponse.user.id, authResponse.user.email)
                Result.success(authResponse.user)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 登出
     */
    suspend fun logout() {
        tokenManager.clearToken()
    }

    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    /**
     * 获取当前用户
     */
    suspend fun getCurrentUser(): Result<UserDTO> {
        return try {
            val response = authApi.getCurrentUser()
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to get user info"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
