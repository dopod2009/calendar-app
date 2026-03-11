package com.calendar.feature.calendar.components

import androidx.compose.animation.core.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calendar.core.common.util.ChineseCalendarHelper
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

// 本地颜色定义
private val WeekendDay = Color(0xFFE57373)
private val OtherMonthDay = Color(0xFFBDBDBD)
private val SelectedDayBackground = Color(0xFF2196F3)
private val TodayBackground = Color(0xFFE3F2FD)
private val EventIndicator = Color(0xFF4CAF50)

/**
 * 月视图日历组件
 */
@Composable
fun MonthCalendarView(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    eventDates: Set<LocalDate> = emptySet()
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // 星期标题行
        WeekDayHeader()
        
        // 日期网格
        MonthGrid(
            yearMonth = yearMonth,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            eventDates = eventDates
        )
    }
}

/**
 * 星期标题行
 */
@Composable
private fun WeekDayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        val weekDays = listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )
        
        weekDays.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.CHINA),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = if (dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.SATURDAY) {
                    WeekendDay
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 月历网格
 */
@Composable
private fun MonthGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    eventDates: Set<LocalDate>
) {
    val today = LocalDate.now()
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    
    // 计算第一天是星期几（周日=0）
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    
    // 计算需要显示的上个月的天数
    val daysFromPreviousMonth = firstDayOfWeek
    
    // 计算总行数
    val totalDays = daysFromPreviousMonth + yearMonth.lengthOfMonth()
    val rows = kotlin.math.ceil(totalDays / 7.0).toInt()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (col in 0 until 7) {
                    val dayIndex = row * 7 + col
                    val date = when {
                        dayIndex < daysFromPreviousMonth -> {
                            // 上个月的日期
                            firstDayOfMonth.minusDays((daysFromPreviousMonth - dayIndex).toLong())
                        }
                        dayIndex < daysFromPreviousMonth + yearMonth.lengthOfMonth() -> {
                            // 当前月的日期
                            firstDayOfMonth.plusDays((dayIndex - daysFromPreviousMonth).toLong())
                        }
                        else -> {
                            // 下个月的日期
                            lastDayOfMonth.plusDays((dayIndex - daysFromPreviousMonth - yearMonth.lengthOfMonth() + 1).toLong())
                        }
                    }
                    
                    val isCurrentMonth = date.month == yearMonth.month
                    val isToday = date == today
                    val isSelected = date == selectedDate
                    val hasEvent = date in eventDates
                    val isWeekend = date.dayOfWeek == DayOfWeek.SUNDAY || date.dayOfWeek == DayOfWeek.SATURDAY
                    
                    DayCell(
                        date = date,
                        isCurrentMonth = isCurrentMonth,
                        isToday = isToday,
                        isSelected = isSelected,
                        hasEvent = hasEvent,
                        isWeekend = isWeekend,
                        onClick = { onDateSelected(date) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}

/**
 * 日期单元格
 */
@Composable
private fun DayCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    hasEvent: Boolean,
    isWeekend: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> SelectedDayBackground
            isToday -> TodayBackground
            else -> Color.Transparent
        },
        animationSpec = tween(200),
        label = "backgroundColor"
    )
    
    val textColor = when {
        isSelected -> Color.White
        !isCurrentMonth -> OtherMonthDay
        isWeekend -> WeekendDay
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    // 获取农历信息
    val lunarDate = remember(date) { ChineseCalendarHelper.solarToLunar(date) }
    val solarTerm = remember(date) { ChineseCalendarHelper.getSolarTerm(date) }
    val festival = remember(date) {
        ChineseCalendarHelper.getTraditionalFestival(lunarDate.month, lunarDate.day, lunarDate.year)
    }
    val gregorianFestival = remember(date) {
        ChineseCalendarHelper.getGregorianFestival(date.monthValue, date.dayOfMonth)
    }
    
    // 农历显示文字（优先级：节日 > 节气 > 农历日）
    val lunarText = when {
        festival.isNotEmpty() -> festival
        gregorianFestival.isNotEmpty() -> gregorianFestival
        solarTerm.isNotEmpty() -> solarTerm
        else -> lunarDate.dayCn
    }
    
    Box(
        modifier = modifier
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 公历日期
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
            
            // 农历日期或节日
            Text(
                text = lunarText,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = textColor.copy(alpha = 0.7f),
                maxLines = 1
            )
            
            // 事件指示器
            if (hasEvent) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(
                            if (isSelected) Color.White else EventIndicator,
                            CircleShape
                        )
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
