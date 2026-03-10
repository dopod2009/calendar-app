package com.calendar.core.data.repository

import com.calendar.core.data.model.EventDao
import com.calendar.core.data.model.EventEntity
import com.calendar.core.data.model.toEntity
import com.calendar.core.data.model.toDomain
import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.network.TokenManager
import com.calendar.core.network.api.SyncApi
import com.calendar.core.network.dto.SyncRequest
import com.calendar.core.network.dto.SyncResponse
import com.calendar.core.network.dto.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 数据同步Repository
 */
@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val syncApi: SyncApi,
    private val eventDao: EventDao,
    private val tokenManager: TokenManager
) {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime

    /**
     * 执行数据同步
     */
    suspend fun sync(): Result<SyncResult> {
        return try {
            _syncState.value = SyncState.Syncing

            // 1. 获取本地修改的事件
            val localChanges = eventDao.getEventsNeedingSync()
            
            // 2. 准备同步请求
            val request = SyncRequest(
                lastSyncTime = _lastSyncTime.value?.toString(),
                events = localChanges.map { it.toNetworkDTO() },
                deviceId = "android_device" // TODO: 获取真实设备ID
            )

            // 3. 调用API同步
            val response = syncApi.syncEvents(request)

            if (response.isSuccessful && response.body()?.success == true) {
                val syncResponse = response.body()?.data!!
                
                // 4. 处理服务器返回的事件
                processServerEvents(syncResponse.events)
                
                // 5. 处理冲突
                if (syncResponse.conflicts?.isNotEmpty() == true) {
                    _syncState.value = SyncState.Conflict(syncResponse.conflicts.size)
                    return Result.success(
                        SyncResult(
                            success = false,
                            conflicts = syncResponse.conflicts.map { it.toDomain() },
                            message = "发现${syncResponse.conflicts.size}个冲突"
                        )
                    )
                }

                // 6. 更新最后同步时间
                _lastSyncTime.value = System.currentTimeMillis()
                _syncState.value = SyncState.Success

                Result.success(
                    SyncResult(
                        success = true,
                        syncedCount = syncResponse.events.size,
                        message = "同步成功"
                    )
                )
            } else {
                _syncState.value = SyncState.Error(response.body()?.message ?: "同步失败")
                Result.failure(Exception(response.body()?.message ?: "Sync failed"))
            }
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "同步异常")
            Result.failure(e)
        }
    }

    /**
     * 处理服务器返回的事件
     */
    private suspend fun processServerEvents(events: List<com.calendar.core.network.dto.EventDTO>) {
        events.forEach { eventDTO ->
            val existingEvent = eventDTO.id?.let { eventDao.getEventById(it) }
            
            if (existingEvent == null) {
                // 新事件，插入到本地
                eventDao.insert(eventDTO.toEntity())
            } else {
                // 更新本地事件
                if (eventDTO.sequence != null && eventDTO.sequence > existingEvent.sequence) {
                    eventDao.update(eventDTO.toEntity())
                }
            }
        }
    }

    /**
     * 获取同步状态
     */
    suspend fun getSyncStatus(): Result<SyncStatusInfo> {
        return try {
            val response = syncApi.getSyncStatus()
            
            if (response.isSuccessful && response.body()?.success == true) {
                val status = response.body()?.data!!
                Result.success(
                    SyncStatusInfo(
                        lastSyncTimestamp = status.lastSyncTimestamp,
                        pendingChanges = status.pendingChanges ?: 0,
                        conflicts = status.conflicts ?: 0
                    )
                )
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to get sync status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 强制全量同步
     */
    suspend fun forceFullSync(): Result<SyncResult> {
        _lastSyncTime.value = null
        return sync()
    }
}

// 状态定义
sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    data class Conflict(val count: Int) : SyncState()
    data class Error(val message: String) : SyncState()
}

data class SyncResult(
    val success: Boolean,
    val syncedCount: Int = 0,
    val conflicts: List<CalendarEvent> = emptyList(),
    val message: String
)

data class SyncStatusInfo(
    val lastSyncTimestamp: Long?,
    val pendingChanges: Int,
    val conflicts: Int
)

// DTO扩展函数
fun EventEntity.toNetworkDTO(): com.calendar.core.network.dto.EventDTO {
    return com.calendar.core.network.dto.EventDTO(
        id = id,
        eventId = eventId,
        title = title,
        description = description,
        location = location,
        startTime = startTime.toString(),
        endTime = endTime.toString(),
        allDay = allDay,
        category = category,
        color = color,
        reminderEnabled = reminderEnabled,
        reminderMinutes = reminderMinutes,
        recurrenceType = recurrenceType,
        recurrenceRule = recurrenceRule,
        sequence = sequence,
        syncStatus = syncStatus,
        lastSyncedAt = lastSyncedAt?.toString()
    )
}

fun com.calendar.core.network.dto.EventDTO.toEntity(): EventEntity {
    return EventEntity(
        id = id ?: 0,
        eventId = eventId ?: "",
        title = title,
        description = description,
        location = location ?: "",
        startTime = LocalDateTime.parse(startTime),
        endTime = LocalDateTime.parse(endTime),
        allDay = allDay,
        category = category ?: "OTHER",
        color = color ?: "#2196F3",
        reminderEnabled = reminderEnabled,
        reminderMinutes = reminderMinutes ?: 15,
        recurrenceType = recurrenceType ?: "NONE",
        recurrenceRule = recurrenceRule ?: "",
        sequence = sequence ?: 1,
        syncStatus = syncStatus ?: "PENDING",
        lastSyncedAt = lastSyncedAt?.let { LocalDateTime.parse(it) }
    )
}
