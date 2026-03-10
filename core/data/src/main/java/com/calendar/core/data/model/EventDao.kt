package com.calendar.core.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * 事件数据访问对象
 */
@Dao
interface EventDao {
    
    // ========== 插入操作 ==========
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)
    
    // ========== 更新操作 ==========
    
    @Update
    suspend fun update(event: EventEntity)
    
    @Query("UPDATE events SET syncStatus = :status WHERE id = :eventId")
    suspend fun updateSyncStatus(eventId: Long, status: SyncStatus)
    
    @Query("UPDATE events SET version = version + 1, updatedAt = :timestamp WHERE id = :eventId")
    suspend fun incrementVersion(eventId: Long, timestamp: Long = System.currentTimeMillis())
    
    // ========== 删除操作 ==========
    
    @Delete
    suspend fun delete(event: EventEntity)
    
    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteById(eventId: Long)
    
    @Query("UPDATE events SET syncStatus = 'DELETED' WHERE id = :eventId")
    suspend fun softDelete(eventId: Long)
    
    // ========== 查询操作 ==========
    
    /**
     * 根据ID查询事件
     */
    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getById(eventId: Long): EventEntity?
    
    @Query("SELECT * FROM events WHERE id = :eventId")
    fun getByIdFlow(eventId: Long): Flow<EventEntity?>
    
    /**
     * 查询指定日期的事件
     */
    @Query("""
        SELECT * FROM events 
        WHERE startDate <= :date 
        AND (endDate IS NULL OR endDate >= :date)
        ORDER BY startTime ASC, title ASC
    """)
    suspend fun getByDate(date: Long): List<EventEntity>
    
    @Query("""
        SELECT * FROM events 
        WHERE startDate <= :date 
        AND (endDate IS NULL OR endDate >= :date)
        ORDER BY startTime ASC, title ASC
    """)
    fun getByDateFlow(date: Long): Flow<List<EventEntity>>
    
    /**
     * 查询日期范围内的事件
     */
    @Query("""
        SELECT * FROM events 
        WHERE startDate >= :startDate 
        AND startDate <= :endDate
        ORDER BY startDate ASC, startTime ASC
    """)
    suspend fun getByDateRange(startDate: Long, endDate: Long): List<EventEntity>
    
    @Query("""
        SELECT * FROM events 
        WHERE startDate >= :startDate 
        AND startDate <= :endDate
        ORDER BY startDate ASC, startTime ASC
    """)
    fun getByDateRangeFlow(startDate: Long, endDate: Long): Flow<List<EventEntity>>
    
    /**
     * 搜索事件（标题或描述）
     */
    @Query("""
        SELECT * FROM events 
        WHERE title LIKE '%' || :keyword || '%' 
        OR description LIKE '%' || :keyword || '%'
        ORDER BY updatedAt DESC
    """)
    suspend fun search(keyword: String): List<EventEntity>
    
    @Query("""
        SELECT * FROM events 
        WHERE title LIKE '%' || :keyword || '%' 
        OR description LIKE '%' || :keyword || '%'
        ORDER BY updatedAt DESC
    """)
    fun searchFlow(keyword: String): Flow<List<EventEntity>>
    
    /**
     * 查询所有事件
     */
    @Query("SELECT * FROM events WHERE syncStatus != 'DELETED' ORDER BY startDate DESC, startTime DESC")
    fun getAllFlow(): Flow<List<EventEntity>>
    
    /**
     * 查询待同步的事件
     */
    @Query("SELECT * FROM events WHERE syncStatus = 'PENDING' OR syncStatus = 'DELETED'")
    suspend fun getPendingSync(): List<EventEntity>
    
    /**
     * 查询有冲突的事件
     */
    @Query("SELECT * FROM events WHERE syncStatus = 'CONFLICT'")
    suspend fun getConflicts(): List<EventEntity>
    
    /**
     * 根据分类查询
     */
    @Query("SELECT * FROM events WHERE category = :category ORDER BY startDate DESC")
    suspend fun getByCategory(category: String): List<EventEntity>
    
    /**
     * 根据日历ID查询
     */
    @Query("SELECT * FROM events WHERE calendarId = :calendarId ORDER BY startDate DESC")
    suspend fun getByCalendarId(calendarId: Long): List<EventEntity>
    
    /**
     * 统计指定日期的事件数量
     */
    @Query("""
        SELECT COUNT(*) FROM events 
        WHERE startDate <= :date 
        AND (endDate IS NULL OR endDate >= :date)
        AND syncStatus != 'DELETED'
    """)
    suspend fun getCountByDate(date: Long): Int
    
    /**
     * 批量获取有事件的日期
     */
    @Query("""
        SELECT DISTINCT startDate FROM events 
        WHERE startDate >= :startDate 
        AND startDate <= :endDate
        AND syncStatus != 'DELETED'
    """)
    suspend fun getDatesWithEvents(startDate: Long, endDate: Long): List<Long>
    
    /**
     * 根据远程ID查询
     */
    @Query("SELECT * FROM events WHERE remoteId = :remoteId")
    suspend fun getByRemoteId(remoteId: String): EventEntity?
}
