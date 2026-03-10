package com.calendar.core.domain.model

import java.time.LocalDate
import java.time.YearMonth

/**
 * 日历月份数据模型
 * 包含一个月的所有日期数据
 */
data class CalendarMonth(
    val yearMonth: YearMonth,
    val days: List<CalendarDay>,
    val previousMonthDays: List<CalendarDay>,  // 上月显示的日期
    val nextMonthDays: List<CalendarDay>        // 下月显示的日期
) {
    /**
     * 获取所有需要显示的日期（包含上月和下月的填充日期）
     */
    fun getAllDisplayDays(): List<CalendarDay> {
        return previousMonthDays + days + nextMonthDays
    }
    
    companion object {
        /**
         * 计算指定月份的周数
         */
        fun getWeekCount(yearMonth: YearMonth): Int {
            val firstDay = yearMonth.atDay(1)
            val lastDay = yearMonth.atEndOfMonth()
            
            // 计算第一天是周几（1=周一，7=周日）
            val firstDayOfWeek = firstDay.dayOfWeek.value
            // 计算最后一天是周几
            val lastDayOfWeek = lastDay.dayOfWeek.value
            
            // 计算总天数加上前面的偏移和后面的偏移
            val totalDays = yearMonth.lengthOfMonth()
            val offsetDays = firstDayOfWeek - 1 // 前面需要填充的天数
            val remainingDays = 7 - lastDayOfWeek // 后面需要填充的天数
            
            return ((totalDays + offsetDays + remainingDays) / 7)
        }
    }
}
