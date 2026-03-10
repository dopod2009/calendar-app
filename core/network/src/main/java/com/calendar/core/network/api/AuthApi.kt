package com.calendar.core.network.api

import com.calendar.core.network.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 认证API接口
 */
interface AuthApi {

    /**
     * 用户注册
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<AuthResponse>>

    /**
     * 用户登录
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthResponse>>

    /**
     * 刷新Token
     */
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<ApiResponse<AuthResponse>>

    /**
     * 获取当前用户信息
     */
    @GET("auth/me")
    suspend fun getCurrentUser(): Response<ApiResponse<UserDTO>>
}
