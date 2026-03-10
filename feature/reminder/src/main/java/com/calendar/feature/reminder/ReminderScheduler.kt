package com.calendar.feature.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.calendar.core.data.model.ReminderEntity
import com.calendar.core.domain.model.ReminderStatus
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * 提醒调度器 - 使用AlarmManager实现精确提醒
 */
class ReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * 调度提醒
     */
    fun schedule(reminder: ReminderEntity) {
        if (reminder.status != ReminderStatus.PENDING) return

        val triggerTime = reminder.reminderTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

        val intent = ReminderReceiver.createIntent(context, reminder.id, reminder.eventId, reminder.message)
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ 需要检查精确闹钟权限
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    // 使用不精确闹钟
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // 处理权限问题
            e.printStackTrace()
        }
    }

    /**
     * 取消提醒
     */
    fun cancel(reminderId: Long) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * 重新调度提醒
     */
    fun reschedule(reminder: ReminderEntity) {
        cancel(reminder.id)
        schedule(reminder)
    }

    /**
     * 批量调度提醒
     */
    fun scheduleAll(reminders: List<ReminderEntity>) {
        reminders.forEach { schedule(it) }
    }

    /**
     * 批量取消提醒
     */
    fun cancelAll(reminderIds: List<Long>) {
        reminderIds.forEach { cancel(it) }
    }

    companion object {
        const val ACTION_REMINDER = "com.calendar.ACTION_REMINDER"
        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_EVENT_ID = "event_id"
        const val EXTRA_MESSAGE = "message"
    }
}
