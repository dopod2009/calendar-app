package com.calendar.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

/**
 * API统一响应格式
 */
@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: T? = null,
    @Json(name = "timestamp") val timestamp: String? = null
)

/**
 * 认证响应
 */
@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "token") val token: String,
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "tokenType") val tokenType: String,
    @Json(name = "expiresIn") val expiresIn: Long,
    @Json(name = "user") val user: UserDTO
)

/**
 * 用户DTO
 */
@JsonClass(generateAdapter = true)
data class UserDTO(
    @Json(name = "id") val id: Long,
    @Json(name = "email") val email: String,
    @Json(name = "username") val username: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "timezone") val timezone: String? = null
)

/**
 * 登录请求
 */
@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "deviceId") val deviceId: String? = null,
    @Json(name = "platform") val platform: String? = null
)

/**
 * 注册请求
 */
@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "username") val username: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "deviceId") val deviceId: String? = null,
    @Json(name = "platform") val platform: String? = null
)

/**
 * 刷新Token请求
 */
@JsonClass(generateAdapter = true)
data class RefreshTokenRequest(
    @Json(name = "refreshToken") val refreshToken: String
)
