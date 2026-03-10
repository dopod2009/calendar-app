package com.calendar.feature.integration.google

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Google Calendar 集成 ViewModel
 */
class GoogleCalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val googleCalendarService = GoogleCalendarService(application)

    // UI 状态
    private val _uiState = MutableStateFlow<GoogleCalendarUiState>(GoogleCalendarUiState.Idle)
    val uiState: StateFlow<GoogleCalendarUiState> = _uiState.asStateFlow()

    // 日历列表
    private val _calendars = MutableStateFlow<List<CalendarInfo>>(emptyList())
    val calendars: StateFlow<List<CalendarInfo>> = _calendars.asStateFlow()

    // 事件列表
    private val _events = MutableStateFlow<List<GoogleCalendarEvent>>(emptyList())
    val events: StateFlow<List<GoogleCalendarEvent>> = _events.asStateFlow()

    // 同步状态
    private val _syncProgress = MutableStateFlow<SyncProgress?>(null)
    val syncProgress: StateFlow<SyncProgress?> = _syncProgress.asStateFlow()

    /**
     * 检查登录状态
     */
    fun checkSignInStatus(): Boolean {
        return googleCalendarService.isSignedIn()
    }

    /**
     * 获取 Google Sign-In Intent
     */
    fun getSignInIntent(): Intent {
        val gso = googleCalendarService.getGoogleSignInOptions()
        val client = GoogleSignIn.getClient(getApplication(), gso)
        return client.signInIntent
    }

    /**
     * 处理登录结果
     */
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>): Boolean {
        return try {
            val account = completedTask.getResult(ApiException::class.java)
            val initialized = googleCalendarService.initialize(account)
            
            if (initialized) {
                _uiState.value = GoogleCalendarUiState.SignedIn(account.email ?: "")
                loadCalendars()
                true
            } else {
                _uiState.value = GoogleCalendarUiState.Error("初始化 Google Calendar 失败")
                false
            }
        } catch (e: ApiException) {
            _uiState.value = GoogleCalendarUiState.Error("登录失败: ${e.statusCode}")
            false
        }
    }

    /**
     * 加载日历列表
     */
    fun loadCalendars() {
        viewModelScope.launch {
            _uiState.value = GoogleCalendarUiState.Loading
            
            val result = googleCalendarService.getCalendarList()
            
            result.fold(
                onSuccess = { calendarList ->
                    _calendars.value = calendarList
                    _uiState.value = GoogleCalendarUiState.SignedIn("已加载 ${calendarList.size} 个日历")
                },
                onFailure = { error ->
                    _uiState.value = GoogleCalendarUiState.Error(error.message ?: "加载失败")
                }
            )
        }
    }

    /**
     * 加载事件
     */
    fun loadEvents(
        calendarId: String = "primary",
        startTime: Long = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000, // 过去30天
        endTime: Long = System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000   // 未来90天
    ) {
        viewModelScope.launch {
            _uiState.value = GoogleCalendarUiState.Loading
            
            val result = googleCalendarService.getEvents(calendarId, startTime, endTime)
            
            result.fold(
                onSuccess = { eventList ->
                    _events.value = eventList
                    _uiState.value = GoogleCalendarUiState.EventsLoaded(eventList.size)
                },
                onFailure = { error ->
                    _uiState.value = GoogleCalendarUiState.Error(error.message ?: "加载失败")
                }
            )
        }
    }

    /**
     * 创建事件
     */
    fun createEvent(event: GoogleCalendarEvent, calendarId: String = "primary") {
        viewModelScope.launch {
            _uiState.value = GoogleCalendarUiState.Loading
            
            val result = googleCalendarService.createEvent(calendarId, event)
            
            result.fold(
                onSuccess = { createdEvent ->
                    _events.value = _events.value + createdEvent
                    _uiState.value = GoogleCalendarUiState.EventCreated(createdEvent)
                },
                onFailure = { error ->
                    _uiState.value = GoogleCalendarUiState.Error(error.message ?: "创建失败")
                }
            )
        }
    }

    /**
     * 更新事件
     */
    fun updateEvent(event: GoogleCalendarEvent, calendarId: String = "primary") {
        viewModelScope.launch {
            _uiState.value = GoogleCalendarUiState.Loading
            
            val eventId = event.googleEventId ?: return@launch
            
            val result = googleCalendarService.updateEvent(calendarId, eventId, event)
            
            result.fold(
                onSuccess = { updatedEvent ->
                    _events.value = _events.value.map {
                        if (it.googleEventId == updatedEvent.googleEventId) updatedEvent else it
                    }
                    _uiState.value = GoogleCalendarUiState.EventUpdated(updatedEvent)
                },
                onFailure = { error ->
                    _uiState.value = GoogleCalendarUiState.Error(error.message ?: "更新失败")
                }
            )
        }
    }

    /**
     * 删除事件
     */
    fun deleteEvent(eventId: String, calendarId: String = "primary") {
        viewModelScope.launch {
            _uiState.value = GoogleCalendarUiState.Loading
            
            val result = googleCalendarService.deleteEvent(calendarId, eventId)
            
            result.fold(
                onSuccess = {
                    _events.value = _events.value.filter { it.googleEventId != eventId }
                    _uiState.value = GoogleCalendarUiState.EventDeleted
                },
                onFailure = { error ->
                    _uiState.value = GoogleCalendarUiState.Error(error.message ?: "删除失败")
                }
            )
        }
    }

    /**
     * 导入 Google Calendar 事件到本地
     */
    fun importFromGoogle(
        calendarId: String = "primary",
        startTime: Long,
        endTime: Long,
        onImportComplete: (List<GoogleCalendarEvent>) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = GoogleCalendarUiState.Loading
            _syncProgress.value = SyncProgress(status = "正在导入...", 0, 0)
            
            googleCalendarService.importEvents(
                calendarId = calendarId,
                startTime = startTime,
                endTime = endTime,
                onImport = { events ->
                    onImportComplete(events)
                    _syncProgress.value = SyncProgress("导入完成", events.size, events.size)
                }
            ).fold(
                onSuccess = { count ->
                    _uiState.value = GoogleCalendarUiState.ImportComplete(count)
                },
                onFailure = { error ->
                    _uiState.value = GoogleCalendarUiState.Error(error.message ?: "导入失败")
                }
            )
        }
    }

    /**
     * 导出本地事件到 Google Calendar
     */
    fun exportToGoogle(
        events: List<GoogleCalendarEvent>,
        calendarId: String = "primary"
    ) {
        viewModelScope.launch {
            _uiState.value = GoogleCalendarUiState.Loading
            
            googleCalendarService.exportEvents(
                calendarId = calendarId,
                events = events,
                onProgress = { current, total ->
                    _syncProgress.value = SyncProgress("正在导出...", current, total)
                }
            ).fold(
                onSuccess = { count ->
                    _uiState.value = GoogleCalendarUiState.ExportComplete(count)
                },
                onFailure = { error ->
                    _uiState.value = GoogleCalendarUiState.Error(error.message ?: "导出失败")
                }
            )
        }
    }

    /**
     * 双向同步
     */
    fun syncWithGoogle(
        calendarId: String = "primary",
        localEvents: List<GoogleCalendarEvent>,
        lastSyncTime: Long,
        onSyncComplete: (SyncResult) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = GoogleCalendarUiState.Syncing
            _syncProgress.value = SyncProgress("正在同步...", 0, 0)
            
            googleCalendarService.syncWithGoogleCalendar(
                calendarId = calendarId,
                localEvents = localEvents,
                lastSyncTime = lastSyncTime
            ).fold(
                onSuccess = { syncResult ->
                    onSyncComplete(syncResult)
                    _syncProgress.value = SyncProgress(
                        "同步完成",
                        syncResult.uploadedCount + syncResult.downloadedEvents.size,
                        syncResult.uploadedCount + syncResult.downloadedEvents.size
                    )
                    _uiState.value = GoogleCalendarUiState.SyncComplete(syncResult)
                },
                onFailure = { error ->
                    _uiState.value = GoogleCalendarUiState.Error(error.message ?: "同步失败")
                }
            )
        }
    }

    /**
     * 登出
     */
    fun signOut() {
        googleCalendarService.signOut()
        _uiState.value = GoogleCalendarUiState.Idle
        _calendars.value = emptyList()
        _events.value = emptyList()
    }
}

/**
 * UI 状态
 */
sealed class GoogleCalendarUiState {
    object Idle : GoogleCalendarUiState()
    object Loading : GoogleCalendarUiState()
    data class SignedIn(val email: String) : GoogleCalendarUiState()
    data class EventsLoaded(val count: Int) : GoogleCalendarUiState()
    data class EventCreated(val event: GoogleCalendarEvent) : GoogleCalendarUiState()
    data class EventUpdated(val event: GoogleCalendarEvent) : GoogleCalendarUiState()
    object EventDeleted : GoogleCalendarUiState()
    data class ImportComplete(val count: Int) : GoogleCalendarUiState()
    data class ExportComplete(val count: Int) : GoogleCalendarUiState()
    object Syncing : GoogleCalendarUiState()
    data class SyncComplete(val result: SyncResult) : GoogleCalendarUiState()
    data class Error(val message: String) : GoogleCalendarUiState()
}

/**
 * 同步进度
 */
data class SyncProgress(
    val status: String,
    val current: Int,
    val total: Int
)
