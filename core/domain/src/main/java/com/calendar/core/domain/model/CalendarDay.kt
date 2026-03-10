package com.calendar.core.domain.model

import java.time.LocalDate

/**
 * 日历日期数据模型
 * 用于在日历视图中展示单个日期的信息
 */
data class CalendarDay(
    val date: LocalDate,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val isCurrentMonth: Boolean = true,
    val lunarDate: String = "",           // 农历日期（如：初一、十五）
    val lunarMonth: String = "",          // 农历月份（如：正月、腊月）
    val solarTerm: String = "",           // 节气（如：立春、雨水）
    val festival: String = "",            // 节日（如：春节、中秋）
    val gregorianFestival: String = "",   // 公历节日（如：元旦、国庆）
    val hasEvent: Boolean = false,        // 是否有事件
    val eventCount: Int = 0               // 事件数量
) {
    /**
     * 获取显示的农历文本
     * 优先显示节日/节气，其次是农历日期
     */
    fun getLunarDisplayText(): String {
        return when {
            festival.isNotEmpty() -> festival
            solarTerm.isNotEmpty() -> solarTerm
            gregorianFestival.isNotEmpty() -> gregorianFestival
            lunarDate == "初一" -> lunarMonth
            else -> lunarDate
        }
    }
    
    /**
     * 是否是周末
     */
    fun isWeekend(): Boolean {
        val dayOfWeek = date.dayOfWeek.value
        return dayOfWeek == 6 || dayOfWeek == 7 // 周六或周日
    }
}
