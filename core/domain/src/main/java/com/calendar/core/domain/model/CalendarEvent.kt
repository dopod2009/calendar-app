package com.calendar.core.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 日历事件数据模型
 */
data class CalendarEvent(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val startDate: LocalDate,
    val startTime: LocalTime? = null,
    val endDate: LocalDate? = null,
    val endTime: LocalTime? = null,
    val isAllDay: Boolean = false,
    val location: String = "",
    val color: EventColor = EventColor.BLUE,
    val reminder: ReminderType = ReminderType.NONE,
    val repeatRule: RepeatRule = RepeatRule.NONE,
    val calendarId: Long = 0,
    val isSynced: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * 获取事件的完整开始时间
     */
    fun getStartDateTime(): LocalDateTime {
        return LocalDateTime.of(startDate, startTime ?: LocalTime.MIN)
    }
    
    /**
     * 获取事件的完整结束时间
     */
    fun getEndDateTime(): LocalDateTime {
        val endDate = endDate ?: startDate
        return LocalDateTime.of(endDate, endTime ?: LocalTime.MAX)
    }
    
    /**
     * 是否是今天的事件
     */
    fun isToday(): Boolean {
        val today = LocalDate.now()
        return !startDate.isAfter(today) && 
               (endDate == null || !endDate.isBefore(today))
    }
}

/**
 * 事件颜色
 */
enum class EventColor(val hex: String) {
    RED("#F44336"),
    PINK("#E91E63"),
    PURPLE("#9C27B0"),
    DEEP_PURPLE("#673AB7"),
    INDIGO("#3F51B5"),
    BLUE("#2196F3"),
    LIGHT_BLUE("#03A9F4"),
    CYAN("#00BCD4"),
    TEAL("#009688"),
    GREEN("#4CAF50"),
    LIGHT_GREEN("#8BC34A"),
    LIME("#CDDC39"),
    YELLOW("#FFEB3B"),
    AMBER("#FFC107"),
    ORANGE("#FF9800"),
    DEEP_ORANGE("#FF5722")
}

/**
 * 提醒类型
 */
enum class ReminderType(val minutes: Int) {
    NONE(0),
    AT_TIME(0),
    FIVE_MINUTES(5),
    FIFTEEN_MINUTES(15),
    THIRTY_MINUTES(30),
    ONE_HOUR(60),
    TWO_HOURS(120),
    ONE_DAY(1440),
    TWO_DAYS(2880),
    ONE_WEEK(10080)
}

/**
 * 重复规则
 */
enum class RepeatRule {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    CUSTOM
}
