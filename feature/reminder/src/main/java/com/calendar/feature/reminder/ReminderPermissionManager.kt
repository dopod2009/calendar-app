package com.calendar.feature.reminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.calendar.feature.reminder.ReminderWorker

/**
 * 提醒权限管理器
 */
class ReminderPermissionManager(private val activity: ComponentActivity) {

    private var onPermissionResult: ((Boolean) -> Unit)? = null

    private val permissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        onPermissionResult?.invoke(allGranted)
    }

    /**
     * 检查并请求必要的权限
     */
    fun checkAndRequestPermissions(onResult: (Boolean) -> Unit) {
        onPermissionResult = onResult

        val permissionsToRequest = mutableListOf<String>()

        // Android 13+ 通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Android 12+ 精确闹钟权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = activity.getSystemService(android.content.Context.ALARM_SERVICE) 
                as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // 需要引导用户到设置页面开启
                // 这里不能直接请求，需要通过Intent跳转
            }
        }

        if (permissionsToRequest.isEmpty()) {
            onResult(true)
        } else {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    /**
     * 检查是否有通知权限
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * 检查是否可以调度精确闹钟
     */
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = activity.getSystemService(android.content.Context.ALARM_SERVICE) 
                as android.app.AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    /**
     * 初始化提醒系统
     */
    fun initializeReminderSystem() {
        checkAndRequestPermissions { granted ->
            if (granted) {
                // 创建通知渠道
                ReminderNotificationManager(activity).createNotificationChannel()
                
                // 启动定期检查
                ReminderWorker.schedulePeriodic(activity)
                
                // 立即检查一次
                ReminderWorker.checkNow(activity)
            }
        }
    }
}
