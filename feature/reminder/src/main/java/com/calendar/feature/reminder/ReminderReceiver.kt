package com.calendar.feature.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 提醒广播接收器
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(ReminderScheduler.EXTRA_REMINDER_ID, -1)
        val eventId = intent.getLongExtra(ReminderScheduler.EXTRA_EVENT_ID, -1)
        val message = intent.getStringExtra(ReminderScheduler.EXTRA_MESSAGE)

        Log.d("ReminderReceiver", "Received reminder: id=$reminderId, eventId=$eventId")

        if (reminderId != -1L && eventId != -1L) {
            // 显示通知
            ReminderNotificationManager(context).showNotification(
                reminderId = reminderId,
                eventId = eventId,
                title = "日历提醒",
                message = message ?: "您有一个即将开始的事件"
            )
        }
    }

    companion object {
        fun createIntent(
            context: Context,
            reminderId: Long,
            eventId: Long,
            message: String?
        ): Intent {
            return Intent(context, ReminderReceiver::class.java).apply {
                action = ReminderScheduler.ACTION_REMINDER
                putExtra(ReminderScheduler.EXTRA_REMINDER_ID, reminderId)
                putExtra(ReminderScheduler.EXTRA_EVENT_ID, eventId)
                putExtra(ReminderScheduler.EXTRA_MESSAGE, message)
            }
        }
    }
}
