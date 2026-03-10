package com.calendar.feature.calendar.util

import com.calendar.core.common.util.ChineseCalendarHelper
import com.calendar.core.domain.model.CalendarDay
import com.calendar.core.domain.model.CalendarMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * 日历工具类
 */
object CalendarUtils {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy年M月")
    
    /**
     * 获取星期标题列表
     */
    fun getWeekDayNames(): List<String> {
        val days = mutableListOf<String>()
        for (day in listOf(
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        )) {
            days.add(day.getDisplayName(TextStyle.SHORT, Locale.CHINA))
        }
        return days
    }
    
    /**
     * 生成指定月份的日历数据
     */
    fun generateMonthData(
        yearMonth: YearMonth,
        selectedDate: LocalDate = LocalDate.now(),
        eventDates: Set<LocalDate> = emptySet()
    ): CalendarMonth {
        val today = LocalDate.now()
        val days = mutableListOf<CalendarDay>()
        val previousMonthDays = mutableListOf<CalendarDay>()
        val nextMonthDays = mutableListOf<CalendarDay>()
        
        // 获取当月第一天和最后一天
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()
        
        // 计算当月第一天是周几（周日=7）
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        
        // 填充上月日期
        if (firstDayOfWeek > 0) {
            val previousMonth = yearMonth.minusMonths(1)
            val previousMonthLastDay = previousMonth.atEndOfMonth()
            
            for (i in firstDayOfWeek - 1 downTo 0) {
                val date = previousMonthLastDay.minusDays(i.toLong())
                previousMonthDays.add(createCalendarDay(date, today, selectedDate, false, eventDates))
            }
        }
        
        // 填充当月日期
        var currentDate = firstDayOfMonth
        while (!currentDate.isAfter(lastDayOfMonth)) {
            days.add(createCalendarDay(currentDate, today, selectedDate, true, eventDates))
            currentDate = currentDate.plusDays(1)
        }
        
        // 填充下月日期
        val lastDayOfWeek = lastDayOfMonth.dayOfWeek.value % 7
        if (lastDayOfWeek < 6) {
            val nextMonth = yearMonth.plusMonths(1)
            for (i in 1..(6 - lastDayOfWeek)) {
                val date = nextMonth.atDay(i)
                nextMonthDays.add(createCalendarDay(date, today, selectedDate, false, eventDates))
            }
        }
        
        return CalendarMonth(yearMonth, days, previousMonthDays, nextMonthDays)
    }
    
    /**
     * 创建单个日历日期数据
     */
    private fun createCalendarDay(
        date: LocalDate,
        today: LocalDate,
        selectedDate: LocalDate,
        isCurrentMonth: Boolean,
        eventDates: Set<LocalDate>
    ): CalendarDay {
        val lunarDate = ChineseCalendarHelper.solarToLunar(date)
        val solarTerm = ChineseCalendarHelper.getSolarTerm(date)
        val festival = ChineseCalendarHelper.getTraditionalFestival(
            lunarDate.month, lunarDate.day, lunarDate.year
        )
        val gregorianFestival = ChineseCalendarHelper.getGregorianFestival(
            date.monthValue, date.dayOfMonth
        )
        
        return CalendarDay(
            date = date,
            isToday = date == today,
            isSelected = date == selectedDate,
            isCurrentMonth = isCurrentMonth,
            lunarDate = lunarDate.dayCn,
            lunarMonth = lunarDate.monthCn,
            solarTerm = solarTerm,
            festival = festival,
            gregorianFestival = gregorianFestival,
            hasEvent = eventDates.contains(date),
            eventCount = if (eventDates.contains(date)) 1 else 0
        )
    }
    
    /**
     * 格式化年月显示
     */
    fun formatYearMonth(yearMonth: YearMonth): String {
        return yearMonth.format(dateFormatter)
    }
    
    /**
     * 获取指定日期所在周的所有日期
     */
    fun getWeekDates(date: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        val dayOfWeek = date.dayOfWeek.value % 7 // 转换为周日=0
        
        for (i in 0..6) {
            dates.add(date.minusDays(dayOfWeek.toLong()).plusDays(i.toLong()))
        }
        
        return dates
    }
}
