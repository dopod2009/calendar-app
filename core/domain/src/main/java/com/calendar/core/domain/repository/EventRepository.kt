package com.calendar.core.domain.repository

import com.calendar.core.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * 事件仓库接口
 * 定义数据访问的抽象接口
 */
interface EventRepository {
    
    /**
     * 插入事件
     */
    suspend fun insert(event: CalendarEvent): Long
    
    /**
     * 更新事件
     */
    suspend fun update(event: CalendarEvent)
    
    /**
     * 删除事件
     */
    suspend fun delete(event: CalendarEvent)
    
    /**
     * 软删除事件
     */
    suspend fun softDelete(eventId: Long)
    
    /**
     * 根据ID获取事件
     */
    suspend fun getById(eventId: Long): CalendarEvent?
    
    /**
     * 根据ID获取事件（Flow）
     */
    fun getByIdFlow(eventId: Long): Flow<CalendarEvent?>
    
    /**
     * 获取指定日期的事件
     */
    suspend fun getByDate(date: LocalDate): List<CalendarEvent>
    
    /**
     * 获取指定日期的事件（Flow）
     */
    fun getByDateFlow(date: LocalDate): Flow<List<CalendarEvent>>
    
    /**
     * 获取日期范围内的事件
     */
    suspend fun getByDateRange(startDate: LocalDate, endDate: LocalDate): List<CalendarEvent>
    
    /**
     * 获取日期范围内的事件（Flow）
     */
    fun getByDateRangeFlow(startDate: LocalDate, endDate: LocalDate): Flow<List<CalendarEvent>>
    
    /**
     * 搜索事件
     */
    suspend fun search(keyword: String): List<CalendarEvent>
    
    /**
     * 搜索事件（Flow）
     */
    fun searchFlow(keyword: String): Flow<List<CalendarEvent>>
    
    /**
     * 获取所有事件
     */
    fun getAllFlow(): Flow<List<CalendarEvent>>
    
    /**
     * 获取待同步的事件
     */
    suspend fun getPendingSync(): List<CalendarEvent>
    
    /**
     * 获取有冲突的事件
     */
    suspend fun getConflicts(): List<CalendarEvent>
    
    /**
     * 更新同步状态
     */
    suspend fun updateSyncStatus(eventId: Long, isSynced: Boolean)
    
    /**
     * 获取有事件的日期集合
     */
    suspend fun getDatesWithEvents(startDate: LocalDate, endDate: LocalDate): Set<LocalDate>
    
    /**
     * 获取指定日期的事件数量
     */
    suspend fun getCountByDate(date: LocalDate): Int
}
