package com.calendar.core.data.repository

import com.calendar.core.data.model.EventDao
import com.calendar.core.data.model.EventEntity
import com.calendar.core.data.model.toDomain
import com.calendar.core.data.model.toEntity
import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.network.api.EventApi
import com.calendar.core.network.dto.CreateEventRequest
import com.calendar.core.network.dto.UpdateEventRequest
import com.calendar.core.network.dto.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 事件Repository实现 - 支持本地+远程数据源
 */
@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val eventApi: EventApi
) : com.calendar.core.domain.repository.EventRepository {

    /**
     * 获取所有事件（本地优先）
     */
    override fun getAllEvents(): Flow<List<CalendarEvent>> {
        return eventDao.getAllEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * 根据日期获取事件
     */
    override fun getEventsByDate(date: LocalDateTime): Flow<List<CalendarEvent>> {
        val startOfDay = date.toLocalDate().atStartOfDay()
        val endOfDay = startOfDay.plusDays(1)
        
        return eventDao.getEventsBetween(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * 搜索事件
     */
    override fun searchEvents(query: String): Flow<List<CalendarEvent>> {
        return eventDao.searchEvents(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * 添加事件（本地+远程）
     */
    override suspend fun addEvent(event: CalendarEvent): Result<CalendarEvent> {
        return try {
            // 1. 保存到本地数据库
            val entity = event.toEntity()
            val id = eventDao.insert(entity)
            
            // 2. 同步到远程服务器
            try {
                val request = CreateEventRequest(
                    title = event.title,
                    description = event.description,
                    location = event.location,
                    startTime = event.startTime.toString(),
                    endTime = event.endTime.toString(),
                    allDay = event.allDay,
                    category = event.category,
                    color = event.color,
                    reminderEnabled = event.reminderEnabled,
                    reminderMinutes = event.reminderMinutes
                )
                
                val response = eventApi.createEvent(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    val remoteEvent = response.body()?.data!!
                    // 更新本地数据库的远程ID
                    eventDao.update(entity.copy(
                        id = id,
                        eventId = remoteEvent.eventId ?: "",
                        syncStatus = "SYNCED"
                    ))
                }
            } catch (e: Exception) {
                // 远程同步失败，标记为待同步
                eventDao.update(entity.copy(
                    id = id,
                    syncStatus = "PENDING"
                ))
            }
            
            Result.success(event.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 更新事件
     */
    override suspend fun updateEvent(event: CalendarEvent): Result<CalendarEvent> {
        return try {
            val entity = event.toEntity().copy(
                sequence = (event.sequence ?: 0) + 1,
                syncStatus = "PENDING"
            )
            eventDao.update(entity)
            
            // 尝试远程同步
            try {
                val request = UpdateEventRequest(
                    title = event.title,
                    description = event.description,
                    location = event.location,
                    startTime = event.startTime.toString(),
                    endTime = event.endTime.toString(),
                    allDay = event.allDay,
                    category = event.category,
                    color = event.color,
                    reminderEnabled = event.reminderEnabled,
                    reminderMinutes = event.reminderMinutes
                )
                
                event.id?.let { id ->
                    val response = eventApi.updateEvent(id, request)
                    if (response.isSuccessful && response.body()?.success == true) {
                        eventDao.update(entity.copy(syncStatus = "SYNCED"))
                    }
                }
            } catch (e: Exception) {
                // 远程同步失败，保持PENDING状态
            }
            
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 删除事件
     */
    override suspend fun deleteEvent(eventId: Long): Result<Unit> {
        return try {
            eventDao.deleteById(eventId)
            
            // 远程删除
            try {
                eventApi.deleteEvent(eventId)
            } catch (e: Exception) {
                // 忽略远程删除失败
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 根据ID获取事件
     */
    override suspend fun getEventById(id: Long): CalendarEvent? {
        return eventDao.getEventById(id)?.toDomain()
    }
}
