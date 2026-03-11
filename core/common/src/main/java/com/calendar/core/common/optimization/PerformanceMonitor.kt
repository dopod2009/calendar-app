package com.calendar.core.common.optimization

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 性能监控工具类
 */
object PerformanceMonitor {

    private const val TAG = "PerformanceMonitor"
    private const val SLOW_THRESHOLD_MS = 500L

    // 性能统计缓存
    private val performanceCache = mutableMapOf<String, MutableList<Long>>()

    /**
     * 监控方法执行时间
     */
    inline fun <T> measureTime(tag: String, operation: String, block: () -> T): T {
        val startTime = System.currentTimeMillis()
        val result = block()
        val duration = System.currentTimeMillis() - startTime

        logPerformance(tag, operation, duration)
        return result
    }

    /**
     * 监控协程执行时间
     */
    suspend inline fun <T> measureTimeSuspend(
        tag: String,
        operation: String,
        crossinline block: suspend () -> T
    ): T {
        val startTime = System.currentTimeMillis()
        val result = withContext(Dispatchers.Default) { block() }
        val duration = System.currentTimeMillis() - startTime

        logPerformance(tag, operation, duration)
        return result
    }

    /**
     * 记录性能日志
     */
    @PublishedApi
    internal fun logPerformance(tag: String, operation: String, duration: Long) {
        val logEntry = "$tag/$operation"

        // 缓存性能数据
        performanceCache.getOrPut(logEntry) { mutableListOf() }.add(duration)

        // 记录慢操作
        if (duration > SLOW_THRESHOLD_MS) {
            Log.w(TAG, "⚠️ SLOW OPERATION: $logEntry took ${duration}ms")
        } else {
            Log.d(TAG, "✓ $logEntry completed in ${duration}ms")
        }
    }

    /**
     * 获取平均执行时间
     */
    fun getAverageTime(tag: String, operation: String): Double {
        val key = "$tag/$operation"
        val times = performanceCache[key] ?: return 0.0
        return times.average()
    }

    /**
     * 获取性能报告
     */
    fun getPerformanceReport(): Map<String, PerformanceStats> {
        return performanceCache.mapValues { (_, times) ->
            PerformanceStats(
                count = times.size,
                averageMs = times.average(),
                minMs = times.minOrNull() ?: 0,
                maxMs = times.maxOrNull() ?: 0,
                totalMs = times.sum()
            )
        }
    }

    /**
     * 清除性能缓存
     */
    fun clearCache() {
        performanceCache.clear()
    }

    /**
     * 导出性能报告到文件
     */
    suspend fun exportReport(context: Context): File? {
        return withContext(Dispatchers.IO) {
            try {
                val report = StringBuilder()
                report.appendLine("Performance Report - ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
                report.appendLine("=".repeat(50))
                report.appendLine()

                getPerformanceReport().forEach { (operation, stats) ->
                    report.appendLine("Operation: $operation")
                    report.appendLine("  Count: ${stats.count}")
                    report.appendLine("  Average: ${String.format("%.2f", stats.averageMs)}ms")
                    report.appendLine("  Min: ${stats.minMs}ms")
                    report.appendLine("  Max: ${stats.maxMs}ms")
                    report.appendLine("  Total: ${stats.totalMs}ms")
                    report.appendLine()
                }

                val file = File(context.cacheDir, "performance_report_${System.currentTimeMillis()}.txt")
                file.writeText(report.toString())
                file
            } catch (e: Exception) {
                Log.e(TAG, "Failed to export performance report", e)
                null
            }
        }
    }
}

/**
 * 性能统计数据
 */
data class PerformanceStats(
    val count: Int,
    val averageMs: Double,
    val minMs: Long,
    val maxMs: Long,
    val totalMs: Long
)
