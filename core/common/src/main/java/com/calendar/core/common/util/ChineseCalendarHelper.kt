package com.calendar.core.common.util

import java.time.LocalDate
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * 中国农历（阴历）计算工具类
 * 支持公历转农历、节气、传统节日计算
 */
object ChineseCalendarHelper {
    
    // 农历年份信息表（1900-2100年）
    // 每个元素表示一年的信息：前12位表示12个月的大小月（1为大月30天，0为小月29天）
    // 后4位表示闰月月份（0表示无闰月）
    private val lunarInfo = longArrayOf(
        0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
        0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
        0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
        0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
        0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
        0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5b0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0,
        0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
        0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6,
        0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
        0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
        0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
        0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
        0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
        0x05aa0, 0x076a3, 0x096d0, 0x04afb, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
        0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0,
        0x14b63, 0x09370, 0x049f8, 0x04970, 0x064b0, 0x168a6, 0x0ea50, 0x06b20, 0x1a6c4, 0x0aae0,
        0x0a2e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0, 0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4,
        0x052d0, 0x0a9b8, 0x0a950, 0x0b4a0, 0x0b6a6, 0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0, 0x052b0,
        0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50, 0x14b55, 0x04b60, 0x0a570, 0x054e4, 0x0d160,
        0x0e968, 0x0d520, 0x0daa0, 0x16aa6, 0x056d0, 0x04ae0, 0x0a9d4, 0x0a2d0, 0x0d150, 0x0f252,
        0x0d520
    )
    
    // 农历月份名称
    private val lunarMonthNames = arrayOf(
        "正月", "二月", "三月", "四月", "五月", "六月",
        "七月", "八月", "九月", "十月", "冬月", "腊月"
    )
    
    // 农历日期名称
    private val lunarDayNames = arrayOf(
        "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
        "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
        "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
    )
    
    // 节气数据
    private val solarTermInfo = intArrayOf(
        0, 21208, 42467, 63836, 85337, 107014, 128867, 150921,
        173149, 195551, 218072, 240693, 263343, 285989, 308563, 331033,
        353350, 375494, 397447, 419210, 440795, 462224, 483532, 504758
    )
    
    // 节气名称
    private val solarTermNames = arrayOf(
        "小寒", "大寒", "立春", "雨水", "惊蛰", "春分",
        "清明", "谷雨", "立夏", "小满", "芒种", "夏至",
        "小暑", "大暑", "立秋", "处暑", "白露", "秋分",
        "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"
    )
    
    // 传统节日
    private val traditionalFestivals = mapOf(
        "1-1" to "春节",
        "1-15" to "元宵节",
        "2-2" to "龙抬头",
        "5-5" to "端午节",
        "7-7" to "七夕",
        "7-15" to "中元节",
        "8-15" to "中秋节",
        "9-9" to "重阳节",
        "12-8" to "腊八节",
        "12-23" to "小年",
        "12-30" to "除夕" // 特殊处理
    )
    
    // 公历节日
    private val gregorianFestivals = mapOf(
        "1-1" to "元旦",
        "2-14" to "情人节",
        "3-8" to "妇女节",
        "3-12" to "植树节",
        "4-1" to "愚人节",
        "5-1" to "劳动节",
        "5-4" to "青年节",
        "6-1" to "儿童节",
        "7-1" to "建党节",
        "8-1" to "建军节",
        "9-10" to "教师节",
        "10-1" to "国庆节",
        "10-31" to "万圣节",
        "11-11" to "双十一",
        "12-25" to "圣诞节"
    )
    
    /**
     * 农历数据类
     */
    data class LunarDate(
        val year: Int,
        val month: Int,
        val day: Int,
        val isLeapMonth: Boolean = false,
        val yearCn: String = "",      // 中文年份（如：甲子年）
        val monthCn: String = "",     // 中文月份（如：正月）
        val dayCn: String = ""        // 中文日期（如：初一）
    )
    
    /**
     * 公历转农历
     */
    fun solarToLunar(date: LocalDate): LunarDate {
        val calendar = GregorianCalendar(date.year, date.monthValue - 1, date.dayOfMonth)
        return solarToLunar(calendar)
    }
    
    /**
     * 公历转农历（Calendar版本）
     */
    fun solarToLunar(calendar: Calendar): LunarDate {
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH) + 1
        var day = calendar.get(Calendar.DAY_OF_MONTH)
        
        // 计算与1900年1月31日的相差天数
        var offset = ((calendar.timeInMillis - getSolarCalendar(1900, 1, 31).timeInMillis) / 86400000L).toInt()
        
        var lunarYear = 1900
        var yearDays: Int
        while (lunarYear < 2101 && offset > 0) {
            yearDays = getLunarYearDays(lunarYear)
            if (offset < yearDays) {
                break
            }
            offset -= yearDays
            lunarYear++
        }
        
        // 计算农历月日
        val leapMonth = getLeapMonth(lunarYear)
        var isLeap = false
        var lunarMonth = 1
        var monthDays: Int
        
        while (lunarMonth < 13 && offset > 0) {
            if (leapMonth > 0 && lunarMonth == (leapMonth + 1) && !isLeap) {
                --lunarMonth
                isLeap = true
                monthDays = getLeapDays(lunarYear)
            } else {
                monthDays = getLunarMonthDays(lunarYear, lunarMonth)
            }
            
            if (isLeap && lunarMonth == (leapMonth + 1)) {
                isLeap = false
            }
            
            if (offset < monthDays) {
                break
            }
            
            offset -= monthDays
            lunarMonth++
        }
        
        val lunarDay = offset + 1
        
        return LunarDate(
            year = lunarYear,
            month = lunarMonth,
            day = lunarDay,
            isLeapMonth = isLeap,
            yearCn = getYearCn(lunarYear),
            monthCn = if (isLeap) "闰${lunarMonthNames[lunarMonth - 1]}" else lunarMonthNames[lunarMonth - 1],
            dayCn = lunarDayNames[lunarDay - 1]
        )
    }
    
    /**
     * 获取农历年的总天数
     */
    private fun getLunarYearDays(year: Int): Int {
        var sum = 348
        for (i in 0x8000 downTo 0x8) {
            sum += if ((lunarInfo[year - 1900].toInt() and i) == 0) 0 else 1
        }
        return sum + getLeapDays(year)
    }
    
    /**
     * 获取农历年闰月的天数
     */
    private fun getLeapDays(year: Int): Int {
        if (getLeapMonth(year) == 0) return 0
        return if ((lunarInfo[year - 1900].toInt() and 0x10000) == 0) 29 else 30
    }
    
    /**
     * 获取农历年闰月月份
     */
    private fun getLeapMonth(year: Int): Int {
        return (lunarInfo[year - 1900].toInt() and 0xf0000) shr 16
    }
    
    /**
     * 获取农历月的天数
     */
    private fun getLunarMonthDays(year: Int, month: Int): Int {
        return if ((lunarInfo[year - 1900].toInt() and (0x10000 shr month)) == 0) 29 else 30
    }
    
    /**
     * 获取年份的中文表示（天干地支）
     */
    private fun getYearCn(year: Int): String {
        val tianGan = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
        val diZhi = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")
        val animals = arrayOf("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪")
        
        val ganIndex = (year - 4) % 10
        val zhiIndex = (year - 4) % 12
        
        return "${tianGan[ganIndex]}${diZhi[zhiIndex]}年(${animals[zhiIndex]}年)"
    }
    
    /**
     * 获取指定日期的节气
     */
    fun getSolarTerm(date: LocalDate): String {
        val baseDate = LocalDate.of(1900, 1, 6)
        var offset = java.time.temporal.ChronoUnit.DAYS.between(baseDate, date)
        
        val year = date.year
        val termIndex = ((year - 1900) * 24)
        
        for (i in 0 until 24) {
            val termOffset = solarTermInfo[i] / 86400
            val termDate = baseDate.plusDays((termIndex + i) * 15L + termOffset)
            
            if (termDate == date) {
                return solarTermNames[i]
            }
        }
        
        return ""
    }
    
    /**
     * 获取传统节日名称
     */
    fun getTraditionalFestival(lunarMonth: Int, lunarDay: Int, year: Int): String {
        val key = "$lunarMonth-$lunarDay"
        
        // 除夕特殊处理（腊月最后一天）
        if (lunarMonth == 12) {
            val lastDay = if (getLeapMonth(year) == 12) {
                getLunarMonthDays(year, 12) + getLeapDays(year)
            } else {
                getLunarMonthDays(year, 12)
            }
            if (lunarDay == lastDay) {
                return "除夕"
            }
        }
        
        return traditionalFestivals[key] ?: ""
    }
    
    /**
     * 获取公历节日名称
     */
    fun getGregorianFestival(month: Int, day: Int): String {
        val key = "$month-$day"
        return gregorianFestivals[key] ?: ""
    }
    
    /**
     * 获取公历Calendar实例
     */
    private fun getSolarCalendar(year: Int, month: Int, day: Int): Calendar {
        return GregorianCalendar(year, month - 1, day)
    }
}
