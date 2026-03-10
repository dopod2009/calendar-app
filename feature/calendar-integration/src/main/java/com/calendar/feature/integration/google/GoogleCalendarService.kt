package com.calendar.feature.integration.google

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.Events
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Google Calendar API 集成服务
 * 功能：
 * - Google账号授权
 * - 获取日历事件
 * - 创建/更新/删除事件
 * - 双向同步
 */
class GoogleCalendarService(private val context: Context) {

    private var calendarService: Calendar? = null
    private var googleAccount: GoogleSignInAccount? = null

    companion object {
        // Google API 客户端ID（需要在 Google Cloud Console 申请）
        private const val CLIENT_ID = "YOUR_CLIENT_ID.apps.googleusercontent.com"
        
        // 申请的权限范围
        private val SCOPES = listOf(CalendarScopes.CALENDAR)
    }

    /**
     * 初始化 Google Sign-In
     */
    fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(CLIENT_ID)
            .requestScopes(com.google.android.gms.common.api.Scope(CalendarScopes.CALENDAR))
            .build()
    }

    /**
     * 检查是否已登录
     */
    fun isSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null
    }

    /**
     * 初始化 Calendar API 客户端
     */
    fun initialize(account: GoogleSignInAccount): Boolean {
        return try {
            googleAccount = account
            
            // 创建 Google Account Credential
            val credential = GoogleAccountCredential.usingOAuth2(
                context,
                SCOPES
            ).apply {
                selectedAccount = account.account
            }

            // 创建 Calendar 服务客户端
            calendarService = Calendar.Builder(
                NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
            )
                .setApplicationName("Calendar App")
                .build()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取所有日历列表
     */
    suspend fun getCalendarList(): Result<List<CalendarInfo>> = withContext(Dispatchers.IO) {
        try {
            val calendars = calendarService?.calendarList()?.list()?.execute()
            val calendarList = calendars?.items?.map { calendar ->
                CalendarInfo(
                    id = calendar.id,
                    summary = calendar.summary,
                    description = calendar.description,
                    primary = calendar.primary ?: false,
                    backgroundColor = calendar.backgroundColor
                )
            } ?: emptyList()

            Result.success(calendarList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取指定日历的事件列表
     */
    suspend fun getEvents(
        calendarId: String = "primary",
        startTime: Long,
        endTime: Long,
        maxResults: Int = 100
    ): Result<List<GoogleCalendarEvent>> = withContext(Dispatchers.IO) {
        try {
            val events: Events = calendarService?.events()?.list(calendarId)
                ?.setTimeMin(DateTime(startTime))
                ?.setTimeMax(DateTime(endTime))
                ?.setMaxResults(maxResults.toLong())
                ?.setOrderBy("startTime")
                ?.setSingleEvents(true)
                ?.execute() ?: throw IllegalStateException("Calendar service not initialized")

            val eventList = events.items.map { event ->
                mapGoogleEventToModel(event)
            }

            Result.success(eventList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 创建事件到 Google Calendar
     */
    suspend fun createEvent(
        calendarId: String = "primary",
        event: GoogleCalendarEvent
    ): Result<GoogleCalendarEvent> = withContext(Dispatchers.IO) {
        try {
            val googleEvent = Event().apply {
                summary = event.title
                description = event.description
                location = event.location
                
                start = EventDateTime().apply {
                    dateTime = DateTime(event.startTime)
                    timeZone = event.timeZone
                }
                
                end = EventDateTime().apply {
                    dateTime = DateTime(event.endTime)
                    timeZone = event.timeZone
                }

                // 设置提醒
                if (event.reminders.isNotEmpty()) {
                    reminders = Event.Reminders().apply {
                        useDefault = false
                        overrides = event.reminders.map { reminder ->
                            EventReminder().apply {
                                method = if (reminder.type == "email") "email" else "popup"
                                minutes = reminder.minutesBefore
                            }
                        }
                    }
                }

                // 设置重复规则
                if (event.recurrenceRule != null) {
                    recurrence = listOf(event.recurrenceRule)
                }

                // 设置颜色
                if (event.colorId != null) {
                    colorId = event.colorId
                }
            }

            val createdEvent = calendarService?.events()?.insert(calendarId, googleEvent)?.execute()
            
            Result.success(mapGoogleEventToModel(createdEvent!!))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 更新 Google Calendar 事件
     */
    suspend fun updateEvent(
        calendarId: String = "primary",
        eventId: String,
        event: GoogleCalendarEvent
    ): Result<GoogleCalendarEvent> = withContext(Dispatchers.IO) {
        try {
            // 先获取现有事件
            val existingEvent = calendarService?.events()?.get(calendarId, eventId)?.execute()
                ?: throw IllegalStateException("Event not found")

            // 更新字段
            existingEvent.apply {
                summary = event.title
                description = event.description
                location = event.location
                
                start = EventDateTime().apply {
                    dateTime = DateTime(event.startTime)
                    timeZone = event.timeZone
                }
                
                end = EventDateTime().apply {
                    dateTime = DateTime(event.endTime)
                    timeZone = event.timeZone
                }
            }

            val updatedEvent = calendarService?.events()?.update(calendarId, eventId, existingEvent)?.execute()
            
            Result.success(mapGoogleEventToModel(updatedEvent!!))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 删除 Google Calendar 事件
     */
    suspend fun deleteEvent(
        calendarId: String = "primary",
        eventId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            calendarService?.events()?.delete(calendarId, eventId)?.execute()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 导入 Google Calendar 事件到本地
     */
    suspend fun importEvents(
        calendarId: String = "primary",
        startTime: Long,
        endTime: Long,
        onImport: suspend (List<GoogleCalendarEvent>) -> Unit
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val eventsResult = getEvents(calendarId, startTime, endTime)
            
            eventsResult.fold(
                onSuccess = { events ->
                    onImport(events)
                    Result.success(events.size)
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 导出本地事件到 Google Calendar
     */
    suspend fun exportEvents(
        calendarId: String = "primary",
        events: List<GoogleCalendarEvent>,
        onProgress: (Int, Int) -> Unit
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            var successCount = 0
            events.forEachIndexed { index, event ->
                val result = createEvent(calendarId, event)
                if (result.isSuccess) {
                    successCount++
                }
                onProgress(index + 1, events.size)
            }
            Result.success(successCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 双向同步
     */
    suspend fun syncWithGoogleCalendar(
        calendarId: String = "primary",
        localEvents: List<GoogleCalendarEvent>,
        lastSyncTime: Long
    ): Result<SyncResult> = withContext(Dispatchers.IO) {
        try {
            // 1. 获取 Google Calendar 的最新事件
            val googleEvents = getEvents(
                calendarId,
                lastSyncTime,
                System.currentTimeMillis()
            ).getOrThrow()

            // 2. 比较并同步
            val toUpload = mutableListOf<GoogleCalendarEvent>()
            val toDownload = mutableListOf<GoogleCalendarEvent>()
            val conflicts = mutableListOf<ConflictInfo>()

            // 检查需要上传的事件
            localEvents.forEach { localEvent ->
                val googleEvent = googleEvents.find { it.googleEventId == localEvent.googleEventId }
                
                if (googleEvent == null) {
                    // 本地新增，需要上传
                    toUpload.add(localEvent)
                } else if (localEvent.updatedAt > googleEvent.updatedAt) {
                    // 本地更新，需要上传
                    toUpload.add(localEvent)
                } else if (googleEvent.updatedAt > localEvent.updatedAt) {
                    // 远程更新，需要下载
                    toDownload.add(googleEvent)
                }
                // 如果时间相同，忽略
            }

            // 检查 Google 新增的事件
            googleEvents.forEach { googleEvent ->
                val localEvent = localEvents.find { it.googleEventId == googleEvent.googleEventId }
                if (localEvent == null) {
                    // Google 新增，需要下载
                    toDownload.add(googleEvent)
                }
            }

            // 3. 执行上传
            val uploadedEvents = mutableListOf<GoogleCalendarEvent>()
            toUpload.forEach { event ->
                val result = createEvent(calendarId, event)
                if (result.isSuccess) {
                    uploadedEvents.add(result.getOrThrow())
                }
            }

            Result.success(
                SyncResult(
                    uploadedCount = uploadedEvents.size,
                    downloadedEvents = toDownload,
                    conflictCount = conflicts.size
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 登出
     */
    fun signOut() {
        GoogleSignIn.getClient(context, getGoogleSignInOptions()).signOut()
        googleAccount = null
        calendarService = null
    }

    /**
     * 映射 Google Event 到模型
     */
    private fun mapGoogleEventToModel(event: Event): GoogleCalendarEvent {
        return GoogleCalendarEvent(
            googleEventId = event.id,
            title = event.summary ?: "",
            description = event.description ?: "",
            location = event.location ?: "",
            startTime = event.start.dateTime?.value ?: event.start.date?.value ?: 0L,
            endTime = event.end.dateTime?.value ?: event.end.date?.value ?: 0L,
            timeZone = event.start.timeZone ?: TimeZone.getDefault().id,
            reminders = event.reminders?.overrides?.map { reminder ->
                EventReminder(
                    type = reminder.method ?: "popup",
                    minutesBefore = reminder.minutes ?: 0
                )
            } ?: emptyList(),
            recurrenceRule = event.recurrence?.firstOrNull(),
            colorId = event.colorId,
            createdAt = event.created?.value ?: 0L,
            updatedAt = event.updated?.value ?: 0L
        )
    }
}

/**
 * 日历信息
 */
data class CalendarInfo(
    val id: String,
    val summary: String,
    val description: String?,
    val primary: Boolean,
    val backgroundColor: String?
)

/**
 * Google Calendar 事件模型
 */
data class GoogleCalendarEvent(
    val googleEventId: String? = null,
    val title: String,
    val description: String = "",
    val location: String = "",
    val startTime: Long,
    val endTime: Long,
    val timeZone: String = TimeZone.getDefault().id,
    val reminders: List<EventReminder> = emptyList(),
    val recurrenceRule: String? = null,
    val colorId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 事件提醒
 */
data class EventReminder(
    val type: String = "popup", // "email" or "popup"
    val minutesBefore: Int = 15
)

/**
 * 同步结果
 */
data class SyncResult(
    val uploadedCount: Int,
    val downloadedEvents: List<GoogleCalendarEvent>,
    val conflictCount: Int
)

/**
 * 冲突信息
 */
data class ConflictInfo(
    val localEvent: GoogleCalendarEvent,
    val remoteEvent: GoogleCalendarEvent
)
