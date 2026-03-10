package com.calendar.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 网络状态拦截器
 */
class NetworkInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        return try {
            val response = chain.proceed(request)
            
            // 检查响应是否成功
            if (!response.isSuccessful) {
                // 可以在这里统一处理错误响应
            }
            
            response
        } catch (e: IOException) {
            // 网络异常处理
            throw NetworkException("Network error: ${e.message}", e)
        }
    }
}

class NetworkException(message: String, cause: Throwable? = null) : IOException(message, cause)
