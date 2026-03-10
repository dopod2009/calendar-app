package com.calendar.core.domain.model

import java.time.LocalDateTime

/**
 * 提醒数据模型
 */
data class Reminder(
    val id: Long = 0,
    val eventId: Long,
    val reminderTime: LocalDateTime,
    val message: String? = null,
    val status: ReminderStatus = ReminderStatus.PENDING,
    val notificationId: Int? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class ReminderStatus {
    PENDING,    // 待触发
    TRIGGERED,  // 已触发
    CANCELLED,  // 已取消
    FAILED      // 失败
}

/**
 * 提醒规则
 */
data class ReminderRule(
    val minutesBefore: Int,  // 事件开始前多少分钟提醒
    val isEnabled: Boolean = true
) {
    companion object {
        // 预设提醒时间
        val AT_TIME = ReminderRule(0)  // 准时
        val FIVE_MINUTES = ReminderRule(5)
        val FIFTEEN_MINUTES = ReminderRule(15)
        val THIRTY_MINUTES = ReminderRule(30)
        val ONE_HOUR = ReminderRule(60)
        val ONE_DAY = ReminderRule(24 * 60)
    }
}

/**
 * 重复规则
 */
data class RecurrenceRule(
    val type: RecurrenceType,
    val interval: Int = 1,  // 间隔
    val endDate: LocalDateTime? = null,  // 结束日期
    val count: Int? = null  // 重复次数
)

enum class RecurrenceType {
    NONE,       // 不重复
    DAILY,      // 每天
    WEEKLY,     // 每周
    MONTHLY,    // 每月
    YEARLY,     // 每年
    CUSTOM      // 自定义
}
