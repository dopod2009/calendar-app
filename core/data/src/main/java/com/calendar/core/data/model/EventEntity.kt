package com.calendar.core.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.domain.model.EventColor
import com.calendar.core.domain.model.ReminderType
import com.calendar.core.domain.model.RepeatRule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 事件实体类（Room数据库表）
 * 与后端数据模型保持一致，便于同步
 */
@Entity(
    tableName = "events",
    indices = [
        Index(value = ["startDate"]),
        Index(value = ["calendarId"]),
        Index(value = ["syncStatus"])
    ]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 基本信息字段
    val title: String,
    val description: String = "",
    
    // 时间字段
    val startDate: Long,              // LocalDate.toEpochDay()
    val startTime: Long? = null,      // LocalTime.toSecondOfDay()
    val endDate: Long? = null,        // LocalDate.toEpochDay()
    val endTime: Long? = null,        // LocalTime.toSecondOfDay()
    val isAllDay: Boolean = false,
    
    // 位置字段
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // 外观字段
    val color: String = EventColor.BLUE.hex,
    
    // 提醒字段
    val reminderType: String = ReminderType.NONE.name,
    val reminderMinutes: Int = 0,
    
    // 重复规则
    val repeatRule: String = RepeatRule.NONE.name,
    val repeatInterval: Int = 0,      // 自定义重复间隔
    val repeatEndDate: Long? = null,  // 重复结束日期
    
    // 分类字段
    val calendarId: Long = 0,
    val category: String = "",        // 事件分类
    
    // 同步字段
    val remoteId: String? = null,     // 服务端ID
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val version: Int = 1,             // 版本号，用于冲突处理
    val lastModified: Long = System.currentTimeMillis(),
    
    // 时区
    val timeZone: String = "Asia/Shanghai",
    
    // 创建和更新时间
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * 转换为领域模型
     */
    fun toDomainModel(): CalendarEvent {
        return CalendarEvent(
            id = id,
            title = title,
            description = description,
            startDate = LocalDate.ofEpochDay(startDate),
            startTime = startTime?.let { LocalTime.ofSecondOfDay(it.toLong()) },
            endDate = endDate?.let { LocalDate.ofEpochDay(it) },
            endTime = endTime?.let { LocalTime.ofSecondOfDay(it.toLong()) },
            isAllDay = isAllDay,
            location = location,
            color = EventColor.values().find { it.hex == color } ?: EventColor.BLUE,
            reminder = ReminderType.valueOf(reminderType),
            repeatRule = RepeatRule.valueOf(repeatRule),
            calendarId = calendarId,
            isSynced = syncStatus == SyncStatus.SYNCED,
            createdAt = LocalDateTime.ofEpochSecond(createdAt / 1000, 0, java.time.ZoneOffset.UTC),
            updatedAt = LocalDateTime.ofEpochSecond(updatedAt / 1000, 0, java.time.ZoneOffset.UTC)
        )
    }
    
    companion object {
        /**
         * 从领域模型创建实体
         */
        fun fromDomainModel(event: CalendarEvent): EventEntity {
            return EventEntity(
                id = event.id,
                title = event.title,
                description = event.description,
                startDate = event.startDate.toEpochDay(),
                startTime = event.startTime?.toSecondOfDay()?.toLong(),
                endDate = event.endDate?.toEpochDay(),
                endTime = event.endTime?.toSecondOfDay()?.toLong(),
                isAllDay = event.isAllDay,
                location = event.location,
                color = event.color.hex,
                reminderType = event.reminder.name,
                reminderMinutes = event.reminder.minutes,
                repeatRule = event.repeatRule.name,
                calendarId = event.calendarId,
                syncStatus = if (event.isSynced) SyncStatus.SYNCED else SyncStatus.PENDING,
                createdAt = event.createdAt.atZone(java.time.ZoneOffset.UTC).toEpochSecond() * 1000,
                updatedAt = event.updatedAt.atZone(java.time.ZoneOffset.UTC).toEpochSecond() * 1000
            )
        }
    }
}

/**
 * 同步状态
 */
enum class SyncStatus {
    PENDING,      // 待同步
    SYNCED,       // 已同步
    CONFLICT,     // 冲突
    DELETED       // 已删除（软删除）
}
