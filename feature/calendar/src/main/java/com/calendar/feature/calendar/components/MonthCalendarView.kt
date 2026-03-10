package com.calendar.feature.calendar.components

import androidx.compose.animation.core.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calendar.app.ui.theme.EventIndicator
import com.calendar.app.ui.theme.OtherMonthDay
import com.calendar.app.ui.theme.SelectedDayBackground
import com.calendar.app.ui.theme.TodayBackground
import com.calendar.app.ui.theme.WeekendDay
import com.calendar.core.domain.model.CalendarDay
import com.calendar.feature.calendar.util.CalendarUtils
import java.time.LocalDate
import java.time.YearMonth

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
    val monthData = remember(yearMonth, eventDates) {
        CalendarUtils.generateMonthData(yearMonth, selectedDate, eventDates)
    }
    val allDays = monthData.getAllDisplayDays()
    
    Column(modifier = modifier) {
        // 星期标题栏
        WeekDayHeader()
        
        // 日期网格
        DateGrid(
            days = allDays,
            onDateSelected = onDateSelected
        )
    }
}

/**
 * 星期标题栏
 */
@Composable
private fun WeekDayHeader() {
    val weekDayNames = CalendarUtils.getWeekDayNames()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDayNames.forEachIndexed { index, dayName ->
            val isWeekend = index == 0 || index == 6
            Text(
                text = dayName,
                style = MaterialTheme.typography.labelSmall,
                color = if (isWeekend) WeekendDay else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 日期网格
 */
@Composable
private fun DateGrid(
    days: List<CalendarDay>,
    onDateSelected: (LocalDate) -> Unit
) {
    val rows = days.chunked(7)
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        rows.forEach { weekDays ->
            DateRow(
                days = weekDays,
                onDateSelected = onDateSelected
            )
        }
    }
}

/**
 * 日期行
 */
@Composable
private fun DateRow(
    days: List<CalendarDay>,
    onDateSelected: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEach { day ->
            DayCell(
                day = day,
                onClick = { onDateSelected(day.date) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 单个日期单元格
 */
@Composable
private fun DayCell(
    day: CalendarDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            day.isSelected -> SelectedDayBackground
            day.isToday -> TodayBackground
            else -> Color.Transparent
        },
        label = "backgroundColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = when {
            day.isSelected -> MaterialTheme.colorScheme.onPrimary
            !day.isCurrentMonth -> OtherMonthDay
            day.isWeekend() -> WeekendDay
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = "textColor"
    )
    
    val lunarTextColor by animateColorAsState(
        targetValue = when {
            day.isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            !day.isCurrentMonth -> OtherMonthDay.copy(alpha = 0.6f)
            day.isWeekend() -> WeekendDay.copy(alpha = 0.7f)
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        },
        label = "lunarTextColor"
    )
    
    Box(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 日期数字
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                fontWeight = if (day.isToday || day.isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp
            )
            
            // 农历日期或节日
            Text(
                text = day.getLunarDisplayText(),
                style = MaterialTheme.typography.labelSmall,
                color = lunarTextColor,
                fontSize = 10.sp,
                maxLines = 1
            )
            
            // 事件指示器
            if (day.hasEvent) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (day.isSelected) Color.White else EventIndicator
                        )
                )
            }
        }
    }
}
