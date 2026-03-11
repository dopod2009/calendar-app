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
import java.time.format.TextStyle
import java.util.Locale

// 本地颜色定义
private val WeekendDay = Color(0xFFE57373)
private val OtherMonthDay = Color(0xFFBDBDBD)
private val SelectedDayBackground = Color(0xFF2196F3)
private val TodayBackground = Color(0xFFE3F2FD)
private val EventIndicator = Color(0xFF4CAF50)

/**
 * 周视图日历组件
 */
@Composable
fun WeekCalendarView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    eventDates: Set<LocalDate> = emptySet()
) {
    val today = LocalDate.now()
    
    // 获取本周的日期（周日开始）
    val startOfWeek = selectedDate.minusDays(selectedDate.dayOfWeek.value % 7L)
    val weekDates = remember(startOfWeek) {
        (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // 星期标题行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            weekDates.forEach { date ->
                val dayOfWeek = date.dayOfWeek
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
        
        // 日期行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDates.forEach { date ->
                val isToday = date == today
                val isSelected = date == selectedDate
                val hasEvent = date in eventDates
                val isWeekend = date.dayOfWeek == DayOfWeek.SUNDAY || date.dayOfWeek == DayOfWeek.SATURDAY
                
                WeekDayCell(
                    date = date,
                    isToday = isToday,
                    isSelected = isSelected,
                    hasEvent = hasEvent,
                    isWeekend = isWeekend,
                    onClick = { onDateSelected(date) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * 周视图日期单元格
 */
@Composable
private fun WeekDayCell(
    date: LocalDate,
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
    
    // 农历显示文字
    val lunarText = when {
        festival.isNotEmpty() -> festival
        gregorianFestival.isNotEmpty() -> gregorianFestival
        solarTerm.isNotEmpty() -> solarTerm
        else -> lunarDate.dayCn
    }
    
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 公历日期
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isToday && !isSelected) TodayBackground else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
        
        // 农历日期或节日
        Text(
            text = lunarText,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
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
        }
    }
}
