package com.calendar.core.network.optimization

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * 网络请求优化工具
 */
object NetworkOptimizations {

    /**
     * 带重试的网络请求
     */
    suspend inline fun <T> retryRequest(
        maxRetries: Int = 3,
        initialDelayMs: Long = 1000,
        crossinline request: suspend () -> Response<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        var lastException: Exception? = null

        repeat(maxRetries) { attempt ->
            try {
                val response = request()
                if (response.isSuccessful && response.body() != null) {
                    return@withContext Result.success(response.body()!!)
                } else {
                    lastException = Exception("HTTP ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                lastException = e
            }

            if (attempt < maxRetries - 1) {
                kotlinx.coroutines.delay(initialDelayMs * (attempt + 1))
            }
        }

        Result.failure(lastException ?: Exception("Unknown error"))
    }

    /**
     * 批量请求合并
     */
    suspend inline fun <T, R> batchRequests(
        items: List<T>,
        batchSize: Int = 10,
        crossinline request: suspend (List<T>) -> List<R>
    ): List<R> = withContext(Dispatchers.IO) {
        if (items.isEmpty()) return@withContext emptyList()

        val results = mutableListOf<R>()

        items.chunked(batchSize).forEach { batch ->
            val batchResults = request(batch)
            results.addAll(batchResults)
        }

        results
    }

    /**
     * 请求去重
     */
    class RequestDeduplicator<T> {
        private val pendingRequests = mutableMapOf<String, kotlinx.coroutines.Deferred<T>>()

        suspend fun execute(
            key: String,
            request: suspend () -> T
        ): T {
            // 检查是否有相同请求正在执行
            pendingRequests[key]?.let {
                return it.await()
            }

            // 创建新请求
            val deferred = kotlinx.coroutines.GlobalScope.async(Dispatchers.IO) {
                try {
                    request()
                } finally {
                    pendingRequests.remove(key)
                }
            }

            pendingRequests[key] = deferred
            return deferred.await()
        }
    }

    /**
     * 请求缓存
     */
    class RequestCache<T>(
        private val ttlMs: Long = 60_000L // 默认1分钟
    ) {
        private data class CacheEntry<T>(
            val data: T,
            val timestamp: Long
        )

        private val cache = mutableMapOf<String, CacheEntry<T>>()

        fun get(key: String): T? {
            val entry = cache[key] ?: return null
            if (System.currentTimeMillis() - entry.timestamp > ttlMs) {
                cache.remove(key)
                return null
            }
            return entry.data
        }

        fun put(key: String, data: T) {
            cache[key] = CacheEntry(data, System.currentTimeMillis())
        }

        fun clear() {
            cache.clear()
        }
    }

    /**
     * 限流器
     */
    class RateLimiter(
        private val intervalMs: Long
    ) {
        private var lastRequestTime = 0L

        suspend fun <T> execute(request: suspend () -> T): T {
            val now = System.currentTimeMillis()
            val timeSinceLastRequest = now - lastRequestTime

            if (timeSinceLastRequest < intervalMs) {
                kotlinx.coroutines.delay(intervalMs - timeSinceLastRequest)
            }

            lastRequestTime = System.currentTimeMillis()
            return request()
        }
    }
}

/**
 * 网络状态监控
 */
object NetworkMonitor {

    /**
     * 检查网络连接
     */
    suspend fun isNetworkAvailable(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val runtime = Runtime.getRuntime()
                val process = runtime.exec("ping -c 1 8.8.8.8")
                process.waitFor() == 0
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * 网络速度测试
     */
    suspend fun testNetworkSpeed(): Long {
        return withContext(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()
                // TODO: 下载测试文件
                val endTime = System.currentTimeMillis()
                endTime - startTime
            } catch (e: Exception) {
                -1L
            }
        }
    }
}
