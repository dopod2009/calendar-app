# Android Manifest 配置（提醒功能）

请在 `app/src/main/AndroidManifest.xml` 中添加以下配置：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- 通知权限 (Android 13+) -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    
    <!-- 精确闹钟权限 (Android 12+) -->
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.USE_EXACT_ALARM" />
    
    <!-- 唤醒设备 -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    
    <!-- 开机自启 -->
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Calendar"
        tools:targetApi="31">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.Calendar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- 提醒广播接收器 -->
        <receiver
            android:name="com.calendar.feature.reminder.ReminderReceiver"
            android:enabled="true"
            android:exported="false">
            <intent-filter>
                <action android:name="com.calendar.ACTION_REMINDER" />
            </intent-filter>
        </receiver>

        <!-- 提醒操作接收器 -->
        <receiver
            android:name="com.calendar.feature.reminder.ReminderActionReceiver"
            android:enabled="true"
            android:exported="false">
            <intent-filter>
                <action android:name="com.calendar.ACTION_DISMISS" />
                <action android:name="com.calendar.ACTION_SNOOZE" />
                <action android:name="com.calendar.ACTION_VIEW_EVENT" />
            </intent-filter>
        </receiver>

        <!-- 开机自启接收器 -->
        <receiver
            android:name="com.calendar.feature.reminder.BootReceiver"
            android:enabled="true"
            android:exported="false">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <action android:name="android.intent.action.QUICKBOOT_POWERON" />
            </intent-filter>
        </receiver>

    </application>

</manifest>
```

## BootReceiver 实现

```kotlin
package com.calendar.feature.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 开机自启接收器 - 重新调度所有待处理的提醒
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device booted, rescheduling reminders")
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val reminderHelper = ReminderHelper(context)
                    reminderHelper.reschedulePendingReminders()
                    
                    // 启动定期检查
                    ReminderWorker.schedulePeriodic(context)
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Failed to reschedule reminders", e)
                }
            }
        }
    }
}
```
