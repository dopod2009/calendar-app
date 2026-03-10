package com.calendar.core.data.optimization

import android.database.sqlite.SQLiteException
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * 数据库优化工具
 */
object DatabaseOptimizations {

    /**
     * 批量插入优化
     */
    suspend inline fun <reified T, R> batchInsert(
        items: List<T>,
        batchSize: Int = 100,
        crossinline insertOperation: suspend (List<T>) -> List<Long>
    ): List<Long> = withContext(Dispatchers.IO) {
        if (items.isEmpty()) return@withContext emptyList()

        val results = mutableListOf<Long>()
        
        items.chunked(batchSize).forEach { batch ->
            val batchResults = insertOperation(batch)
            results.addAll(batchResults)
        }

        results
    }

    /**
     * 批量更新优化
     */
    suspend inline fun <reified T> batchUpdate(
        items: List<T>,
        batchSize: Int = 100,
        crossinline updateOperation: suspend (List<T>) -> Unit
    ) = withContext(Dispatchers.IO) {
        if (items.isEmpty()) return@withContext

        items.chunked(batchSize).forEach { batch ->
            updateOperation(batch)
        }
    }

    /**
     * 事务执行优化
     */
    suspend inline fun <R> executeInTransaction(
        database: RoomDatabase,
        crossinline block: suspend () -> R
    ): R = withContext(Dispatchers.IO) {
        database.withTransaction {
            block()
        }
    }

    /**
     * 重试机制
     */
    suspend inline fun <R> retryDatabaseOperation(
        maxRetries: Int = 3,
        initialDelayMs: Long = 100,
        crossinline operation: suspend () -> R
    ): R {
        var lastException: SQLiteException? = null
        
        repeat(maxRetries) { attempt ->
            try {
                return operation()
            } catch (e: SQLiteException) {
                lastException = e
                if (attempt < maxRetries - 1) {
                    kotlinx.coroutines.delay(initialDelayMs * (attempt + 1))
                }
            }
        }

        throw lastException ?: RuntimeException("Unknown database error")
    }
}

/**
 * 查询优化扩展函数
 */
object QueryOptimizations {

    /**
     * 分页查询
     */
    inline fun <T : Any> Flow<List<T>>.paginate(
        pageSize: Int = 20,
        crossinline loadMore: suspend (Int) -> List<T>
    ): Flow<List<T>> = flowOn(Dispatchers.IO)
}
