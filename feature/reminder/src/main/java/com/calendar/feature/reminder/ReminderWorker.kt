package com.calendar.feature.reminder

import android.content.Context
import androidx.work.*
import com.calendar.core.data.model.ReminderEntity
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * 提醒Worker - 使用WorkManager处理后台提醒检查
 */
class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 检查并触发即将到期的提醒
            val reminderHelper = ReminderHelper(applicationContext)
            reminderHelper.checkAndTriggerReminders()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "reminder_check_work"
        const val WORK_TAG = "reminder"

        /**
         * 启动定期检查（每15分钟）
         */
        fun schedulePeriodic(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .addTag(WORK_TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        /**
         * 立即执行一次检查
         */
        fun checkNow(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .addTag(WORK_TAG)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }

        /**
         * 取消所有提醒检查
         */
        fun cancelAll(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
        }
    }
}
