package com.calendar.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

/**
 * 事件DTO
 */
@JsonClass(generateAdapter = true)
data class EventDTO(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "eventId") val eventId: String? = null,
    @Json(name = "userId") val userId: Long? = null,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "location") val location: String? = null,
    @Json(name = "startTime") val startTime: String,
    @Json(name = "endTime") val endTime: String,
    @Json(name = "allDay") val allDay: Boolean = false,
    @Json(name = "category") val category: String? = null,
    @Json(name = "color") val color: String? = null,
    @Json(name = "reminderEnabled") val reminderEnabled: Boolean = true,
    @Json(name = "reminderMinutes") val reminderMinutes: Int? = null,
    @Json(name = "recurrenceType") val recurrenceType: String? = null,
    @Json(name = "recurrenceRule") val recurrenceRule: String? = null,
    @Json(name = "sequence") val sequence: Long? = null,
    @Json(name = "source") val source: String? = null,
    @Json(name = "syncStatus") val syncStatus: String? = null,
    @Json(name = "lastSyncedAt") val lastSyncedAt: String? = null,
    @Json(name = "externalEventId") val externalEventId: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "updatedAt") val updatedAt: String? = null
)

/**
 * 创建事件请求
 */
@JsonClass(generateAdapter = true)
data class CreateEventRequest(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "location") val location: String? = null,
    @Json(name = "startTime") val startTime: String,
    @Json(name = "endTime") val endTime: String,
    @Json(name = "allDay") val allDay: Boolean = false,
    @Json(name = "category") val category: String? = null,
    @Json(name = "color") val color: String? = null,
    @Json(name = "reminderEnabled") val reminderEnabled: Boolean = true,
    @Json(name = "reminderMinutes") val reminderMinutes: Int? = null,
    @Json(name = "recurrenceType") val recurrenceType: String? = null,
    @Json(name = "recurrenceRule") val recurrenceRule: String? = null
)

/**
 * 更新事件请求
 */
@JsonClass(generateAdapter = true)
data class UpdateEventRequest(
    @Json(name = "title") val title: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "location") val location: String? = null,
    @Json(name = "startTime") val startTime: String? = null,
    @Json(name = "endTime") val endTime: String? = null,
    @Json(name = "allDay") val allDay: Boolean? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "color") val color: String? = null,
    @Json(name = "reminderEnabled") val reminderEnabled: Boolean? = null,
    @Json(name = "reminderMinutes") val reminderMinutes: Int? = null,
    @Json(name = "recurrenceType") val recurrenceType: String? = null,
    @Json(name = "recurrenceRule") val recurrenceRule: String? = null
)

/**
 * 分页响应
 */
@JsonClass(generateAdapter = true)
data class PageResponse<T>(
    @Json(name = "content") val content: List<T>,
    @Json(name = "currentPage") val currentPage: Int,
    @Json(name = "pageSize") val pageSize: Int,
    @Json(name = "totalElements") val totalElements: Long,
    @Json(name = "totalPages") val totalPages: Int,
    @Json(name = "hasNext") val hasNext: Boolean,
    @Json(name = "hasPrevious") val hasPrevious: Boolean
)
