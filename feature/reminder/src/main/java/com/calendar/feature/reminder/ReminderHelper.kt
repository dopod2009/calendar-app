package com.calendar.feature.reminder

import android.content.Context
import android.util.Log
import com.calendar.core.data.model.ReminderDao
import com.calendar.core.data.model.ReminderEntity
import com.calendar.core.data.model.toEntity
import com.calendar.core.domain.model.Reminder
import com.calendar.core.domain.model.ReminderRule
import com.calendar.core.domain.model.ReminderStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

/**
 * 提醒助手 - 统一管理提醒相关操作
 */
class ReminderHelper(private val context: Context) {

    private val database = com.calendar.core.data.model.CalendarDatabase.getInstance(context)
    private val reminderDao: ReminderDao = database.reminderDao()
    private val scheduler = ReminderScheduler(context)
    private val notificationManager = ReminderNotificationManager(context)

    init {
        notificationManager.createNotificationChannel()
    }

    /**
     * 创建提醒
     */
    suspend fun createReminder(
        eventId: Long,
        eventTitle: String,
        eventStartTime: LocalDateTime,
        rule: ReminderRule
    ): Reminder {
        val reminderTime = eventStartTime.minusMinutes(rule.minutesBefore.toLong())
        
        val reminder = Reminder(
            eventId = eventId,
            reminderTime = reminderTime,
            message = "事件「$eventTitle」将在${formatTimeBefore(rule.minutesBefore)}开始",
            status = ReminderStatus.PENDING
        )

        val entity = reminder.toEntity()
        val id = reminderDao.insert(entity)
        
        // 调度系统闹钟
        val savedEntity = entity.copy(id = id)
        scheduler.schedule(savedEntity)

        return reminder.copy(id = id)
    }

    /**
     * 为事件创建多个提醒
     */
    suspend fun createRemindersForEvent(
        eventId: Long,
        eventTitle: String,
        eventStartTime: LocalDateTime,
        rules: List<ReminderRule>
    ): List<Reminder> {
        return rules.filter { it.isEnabled }.map { rule ->
            createReminder(eventId, eventTitle, eventStartTime, rule)
        }
    }

    /**
     * 获取事件的所有提醒
     */
    fun getRemindersByEventId(eventId: Long): Flow<List<Reminder>> {
        return reminderDao.getRemindersByEventId(eventId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * 取消提醒
     */
    suspend fun cancelReminder(reminderId: Long) {
        scheduler.cancel(reminderId)
        reminderDao.updateStatus(reminderId, ReminderStatus.CANCELLED)
    }

    /**
     * 取消事件的所有提醒
     */
    suspend fun cancelEventReminders(eventId: Long) {
        val reminders = reminderDao.getRemindersByEventId(eventId)
        reminders.collect { entities ->
            entities.forEach { entity ->
                scheduler.cancel(entity.id)
            }
            reminderDao.deleteByEventId(eventId)
        }
    }

    /**
     * 检查并触发即将到期的提醒
     */
    suspend fun checkAndTriggerReminders() {
        val now = LocalDateTime.now()
        val pendingReminders = reminderDao.getPendingRemindersBefore(now)

        pendingReminders.forEach { entity ->
            try {
                // 显示通知
                notificationManager.showNotification(
                    reminderId = entity.id,
                    eventId = entity.eventId,
                    title = "日历提醒",
                    message = entity.message ?: "您有一个即将开始的事件"
                )

                // 更新状态
                reminderDao.updateStatus(entity.id, ReminderStatus.TRIGGERED)
                
                Log.d("ReminderHelper", "Triggered reminder: ${entity.id}")
            } catch (e: Exception) {
                Log.e("ReminderHelper", "Failed to trigger reminder: ${entity.id}", e)
                reminderDao.updateStatus(entity.id, ReminderStatus.FAILED)
            }
        }
    }

    /**
     * 重新调度所有待处理的提醒
     */
    suspend fun reschedulePendingReminders() {
        val pendingReminders = reminderDao.getRemindersByStatus(ReminderStatus.PENDING)
        pendingReminders.collect { entities ->
            entities.forEach { entity ->
                scheduler.schedule(entity)
            }
        }
    }

    private fun formatTimeBefore(minutes: Int): String {
        return when {
            minutes == 0 -> "准时"
            minutes < 60 -> "${minutes}分钟后"
            minutes < 24 * 60 -> "${minutes / 60}小时后"
            else -> "${minutes / (24 * 60)}天后"
        }
    }
}

/**
 * Entity转Domain模型
 */
fun ReminderEntity.toDomain(): Reminder {
    return Reminder(
        id = id,
        eventId = eventId,
        reminderTime = reminderTime,
        message = message,
        status = status,
        notificationId = notificationId,
        createdAt = createdAt
    )
}

/**
 * Domain转Entity模型
 */
fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        eventId = eventId,
        reminderTime = reminderTime,
        message = message,
        status = status,
        notificationId = notificationId,
        createdAt = createdAt
    )
}
