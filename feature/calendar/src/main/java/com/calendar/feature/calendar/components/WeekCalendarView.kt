package com.calendar.feature.calendar.components

import androidx.compose.animation.core.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.calendar.core.common.util.ChineseCalendarHelper
import com.calendar.core.domain.model.CalendarDay
import com.calendar.feature.calendar.util.CalendarUtils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

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
    val weekDates = remember(selectedDate) {
        CalendarUtils.getWeekDates(selectedDate)
    }
    
    val today = LocalDate.now()
    
    Column(modifier = modifier) {
        // 星期标题行
        WeekDayHeaderRow(weekDates)
        
        // 日期行
        DateRow(
            weekDates = weekDates,
            selectedDate = selectedDate,
            today = today,
            onDateSelected = onDateSelected,
            eventDates = eventDates
        )
    }
}

/**
 * 星期标题行
 */
@Composable
private fun WeekDayHeaderRow(weekDates: List<LocalDate>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDates.forEach { date ->
            val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
            val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.CHINA)
            
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
 * 日期行
 */
@Composable
private fun DateRow(
    weekDates: List<LocalDate>,
    selectedDate: LocalDate,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    eventDates: Set<LocalDate>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDates.forEach { date ->
            val calendarDay = createCalendarDay(date, today, selectedDate, eventDates)
            WeekDayCell(
                day = calendarDay,
                onClick = { onDateSelected(date) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 周视图日期单元格
 */
@Composable
private fun WeekDayCell(
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
        // 日期数字
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (day.isToday && !day.isSelected) 
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
                    else 
                        Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                fontWeight = if (day.isToday || day.isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 18.sp
            )
        }
        
        // 农历日期
        Text(
            text = day.getLunarDisplayText(),
            style = MaterialTheme.typography.labelSmall,
            color = textColor.copy(alpha = 0.6f),
            fontSize = 10.sp,
            maxLines = 1
        )
        
        // 事件指示器
        if (day.hasEvent) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(
                        if (day.isSelected) Color.White else EventIndicator
                    )
            )
        }
    }
}

/**
 * 创建单个日历日期数据
 */
private fun createCalendarDay(
    date: LocalDate,
    today: LocalDate,
    selectedDate: LocalDate,
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
        isCurrentMonth = date.month == selectedDate.month,
        lunarDate = lunarDate.dayCn,
        lunarMonth = lunarDate.monthCn,
        solarTerm = solarTerm,
        festival = festival,
        gregorianFestival = gregorianFestival,
        hasEvent = eventDates.contains(date),
        eventCount = if (eventDates.contains(date)) 1 else 0
    )
}
