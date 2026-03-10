package com.calendar.feature.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.calendar.app.MainActivity
import java.util.concurrent.atomic.AtomicInteger

/**
 * 提醒通知管理器
 */
class ReminderNotificationManager(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val notificationIdGenerator = AtomicInteger(1000)

    companion object {
        const val CHANNEL_ID_REMINDER = "calendar_reminder"
        const val CHANNEL_NAME = "日历提醒"
        const val CHANNEL_DESCRIPTION = "日历事件提醒通知"
        
        const val ACTION_DISMISS = "com.calendar.ACTION_DISMISS"
        const val ACTION_SNOOZE = "com.calendar.ACTION_SNOOZE"
        const val ACTION_VIEW_EVENT = "com.calendar.ACTION_VIEW_EVENT"
    }

    /**
     * 创建通知渠道（Android 8.0+）
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_REMINDER,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * 显示提醒通知
     */
    fun showNotification(
        reminderId: Long,
        eventId: Long,
        title: String,
        message: String
    ) {
        val notificationId = notificationIdGenerator.incrementAndGet()

        // 点击通知打开App
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("eventId", eventId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDER)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "关闭",
                createDismissIntent(reminderId, notificationId)
            )
            .addAction(
                android.R.drawable.ic_menu_recent_history,
                "稍后提醒",
                createSnoozeIntent(reminderId, eventId, notificationId)
            )
            .build()

        try {
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Android 13+ 需要通知权限
            e.printStackTrace()
        }
    }

    /**
     * 取消通知
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    private fun createDismissIntent(reminderId: Long, notificationId: Int): PendingIntent {
        val intent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = ACTION_DISMISS
            putExtra("reminderId", reminderId)
            putExtra("notificationId", notificationId)
        }
        return PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createSnoozeIntent(
        reminderId: Long,
        eventId: Long,
        notificationId: Int
    ): PendingIntent {
        val intent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra("reminderId", reminderId)
            putExtra("eventId", eventId)
            putExtra("notificationId", notificationId)
        }
        return PendingIntent.getBroadcast(
            context,
            notificationId + 1000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
