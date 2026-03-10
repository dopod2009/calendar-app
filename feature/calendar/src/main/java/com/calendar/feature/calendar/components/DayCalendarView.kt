package com.calendar.feature.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calendar.core.common.util.ChineseCalendarHelper
import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.domain.model.EventColor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * 日视图日历组件
 */
@Composable
fun DayCalendarView(
    date: LocalDate,
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit,
    onAddEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lunarDate = remember(date) { ChineseCalendarHelper.solarToLunar(date) }
    val solarTerm = remember(date) { ChineseCalendarHelper.getSolarTerm(date) }
    val festival = remember(date) {
        ChineseCalendarHelper.getTraditionalFestival(lunarDate.month, lunarDate.day, lunarDate.year)
    }
    val gregorianFestival = remember(date) {
        ChineseCalendarHelper.getGregorianFestival(date.monthValue, date.dayOfMonth)
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // 日期头部信息
        DayHeader(
            date = date,
            lunarDate = lunarDate,
            solarTerm = solarTerm,
            festival = festival,
            gregorianFestival = gregorianFestival
        )
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        // 事件列表
        DayEventList(
            events = events,
            onEventClick = onEventClick,
            onAddEventClick = onAddEventClick
        )
    }
}

/**
 * 日期头部信息
 */
@Composable
private fun DayHeader(
    date: LocalDate,
    lunarDate: ChineseCalendarHelper.LunarDate,
    solarTerm: String,
    festival: String,
    gregorianFestival: String
) {
    val dateFormatter = DateTimeFormatter.ofPattern("M月d日")
    val weekDayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 公历日期和星期
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date.format(dateFormatter),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                )
                
                Text(
                    text = weekDayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 农历信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = lunarDate.yearCn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                
                Text(
                    text = "${lunarDate.monthCn}${lunarDate.dayCn}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            
            // 节日/节气
            if (festival.isNotEmpty() || solarTerm.isNotEmpty() || gregorianFestival.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (festival.isNotEmpty()) {
                        SuggestionChip(
                            onClick = { },
                            label = { Text(festival, fontSize = 12.sp) },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                    if (solarTerm.isNotEmpty()) {
                        SuggestionChip(
                            onClick = { },
                            label = { Text(solarTerm, fontSize = 12.sp) },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                    if (gregorianFestival.isNotEmpty()) {
                        SuggestionChip(
                            onClick = { },
                            label = { Text(gregorianFestival, fontSize = 12.sp) },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 事件列表
 */
@Composable
private fun DayEventList(
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit,
    onAddEventClick: () -> Unit
) {
    if (events.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "暂无事件",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                FilledTonalButton(onClick = onAddEventClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加事件",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("添加事件")
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(events) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }
            
            item {
                FilledTonalButton(
                    onClick = onAddEventClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加事件",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("添加事件")
                }
            }
        }
    }
}

/**
 * 事件卡片
 */
@Composable
private fun EventCard(
    event: CalendarEvent,
    onClick: () -> Unit
) {
    val eventColor = parseEventColor(event.color)
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = eventColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 时间条
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(
                        eventColor,
                        RoundedCornerShape(2.dp)
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                if (!event.isAllDay && event.startTime != null) {
                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    val timeText = buildString {
                        append(event.startTime.format(timeFormatter))
                        if (event.endTime != null) {
                            append(" - ")
                            append(event.endTime.format(timeFormatter))
                        }
                    }
                    
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    Text(
                        text = "全天",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                if (event.location.isNotEmpty()) {
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

/**
 * 解析事件颜色
 */
@Composable
private fun parseEventColor(color: EventColor): Color {
    return try {
        Color(android.graphics.Color.parseColor(color.hex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }
}
