package com.calendar.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 同步请求
 */
@JsonClass(generateAdapter = true)
data class SyncRequest(
    @Json(name = "lastSyncTime") val lastSyncTime: String? = null,
    @Json(name = "events") val events: List<EventDTO>? = null,
    @Json(name = "deviceId") val deviceId: String? = null,
    @Json(name = "platform") val platform: String? = null
)

/**
 * 同步响应
 */
@JsonClass(generateAdapter = true)
data class SyncResponse(
    @Json(name = "events") val events: List<EventDTO>,
    @Json(name = "conflicts") val conflicts: List<EventDTO>? = null,
    @Json(name = "syncTime") val syncTime: String,
    @Json(name = "hasMore") val hasMore: Boolean = false
)

/**
 * 同步状态响应
 */
@JsonClass(generateAdapter = true)
data class SyncStatusResponse(
    @Json(name = "lastSyncTimestamp") val lastSyncTimestamp: Long? = null,
    @Json(name = "pendingChanges") val pendingChanges: Int? = null,
    @Json(name = "conflicts") val conflicts: Int? = null
)
