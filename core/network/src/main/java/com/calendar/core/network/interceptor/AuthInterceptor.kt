package com.calendar.core.network.interceptor

import com.calendar.core.network.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * 认证拦截器 - 自动添加JWT Token到请求头
 */
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 跳过不需要认证的接口
        if (shouldSkipAuth(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }

        // 添加Token到请求头
        val token = tokenManager.getToken()
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(newRequest)

        // Token过期，尝试刷新
        if (response.code == 401) {
            response.close()
            
            synchronized(this) {
                val newToken = refreshToken()
                if (newToken != null) {
                    val retryRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                    return chain.proceed(retryRequest)
                }
            }
            
            // 刷新失败，清除Token
            tokenManager.clearToken()
        }

        return response
    }

    private fun shouldSkipAuth(path: String): Boolean {
        val noAuthPaths = listOf(
            "/auth/login",
            "/auth/register",
            "/health"
        )
        return noAuthPaths.any { path.contains(it) }
    }

    private fun refreshToken(): String? {
        val refreshToken = tokenManager.getRefreshToken() ?: return null
        
        // TODO: 调用刷新Token的API
        // 这里需要同步调用，使用OkHttp
        return null
    }
}
