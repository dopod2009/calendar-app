package com.calendar.feature.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 提醒操作接收器（处理通知按钮点击）
 */
class ReminderActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("reminderId", -1)
        val eventId = intent.getLongExtra("eventId", -1)
        val notificationId = intent.getIntExtra("notificationId", -1)

        when (intent.action) {
            ReminderNotificationManager.ACTION_DISMISS -> {
                // 关闭通知
                ReminderNotificationManager(context).cancelNotification(notificationId)
                Log.d("ReminderAction", "Dismissed reminder: $reminderId")
            }
            ReminderNotificationManager.ACTION_SNOOZE -> {
                // 稍后提醒（5分钟后）
                CoroutineScope(Dispatchers.IO).launch {
                    // TODO: 更新提醒时间到5分钟后
                    ReminderNotificationManager(context).cancelNotification(notificationId)
                    Log.d("ReminderAction", "Snoozed reminder: $reminderId")
                }
            }
        }
    }
}
