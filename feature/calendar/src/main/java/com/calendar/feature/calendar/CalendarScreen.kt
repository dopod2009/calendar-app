package com.calendar.feature.calendar

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calendar.feature.calendar.components.*
import com.calendar.feature.calendar.util.CalendarUtils
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/**
 * 日历主界面
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var currentViewType by remember { mutableStateOf(CalendarViewType.MONTH) }
    val scope = rememberCoroutineScope()
    
    // 模拟事件数据
    val eventDates = remember {
        setOf(
            LocalDate.now(),
            LocalDate.now().plusDays(3),
            LocalDate.now().plusDays(7)
        )
    }
    
    Scaffold(
        topBar = {
            CalendarTopBar(
                yearMonth = currentYearMonth,
                selectedDate = selectedDate,
                currentViewType = currentViewType,
                onViewTypeChanged = { currentViewType = it },
                onTodayClick = {
                    selectedDate = LocalDate.now()
                    currentYearMonth = YearMonth.now()
                },
                onNavigatePrevious = {
                    when (currentViewType) {
                        CalendarViewType.MONTH -> currentYearMonth = currentYearMonth.minusMonths(1)
                        CalendarViewType.WEEK -> selectedDate = selectedDate.minusWeeks(1)
                        CalendarViewType.DAY -> selectedDate = selectedDate.minusDays(1)
                    }
                },
                onNavigateNext = {
                    when (currentViewType) {
                        CalendarViewType.MONTH -> currentYearMonth = currentYearMonth.plusMonths(1)
                        CalendarViewType.WEEK -> selectedDate = selectedDate.plusWeeks(1)
                        CalendarViewType.DAY -> selectedDate = selectedDate.plusDays(1)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Add event */ },
                icon = { Icon(Icons.Default.Add, contentDescription = "添加事件") },
                text = { Text("添加事件") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentViewType) {
                CalendarViewType.MONTH -> {
                    AnimatedCalendarPager(
                        currentViewType = currentViewType,
                        yearMonth = currentYearMonth,
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            selectedDate = date
                            if (date.month != currentYearMonth.month) {
                                currentYearMonth = YearMonth.from(date)
                            }
                        },
                        onYearMonthChanged = { currentYearMonth = it },
                        eventDates = eventDates,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                CalendarViewType.WEEK -> {
                    AnimatedCalendarPager(
                        currentViewType = currentViewType,
                        yearMonth = currentYearMonth,
                        selectedDate = selectedDate,
                        onDateSelected = { selectedDate = it },
                        onYearMonthChanged = { currentYearMonth = it },
                        eventDates = eventDates,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.35f)
                    )
                    
                    // 事件列表
                    EventListSection(
                        selectedDate = selectedDate,
                        modifier = Modifier.weight(0.65f)
                    )
                }
                CalendarViewType.DAY -> {
                    DayCalendarView(
                        date = selectedDate,
                        events = emptyList(), // TODO: Load events for selected date
                        onEventClick = { /* TODO */ },
                        onAddEventClick = { /* TODO */ },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * 日历顶部栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTopBar(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    currentViewType: CalendarViewType,
    onViewTypeChanged: (CalendarViewType) -> Unit,
    onTodayClick: () -> Unit,
    onNavigatePrevious: () -> Unit,
    onNavigateNext: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = when (currentViewType) {
                        CalendarViewType.MONTH -> CalendarUtils.formatYearMonth(yearMonth)
                        CalendarViewType.WEEK -> CalendarUtils.formatYearMonth(YearMonth.from(selectedDate))
                        CalendarViewType.DAY -> "${selectedDate.monthValue}月${selectedDate.dayOfMonth}日"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigatePrevious) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "上一页")
            }
        },
        actions = {
            IconButton(onClick = onNavigateNext) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "下一页")
            }
            
            TextButton(onClick = onTodayClick) {
                Text("今天")
            }
            
            ViewTypeSelector(
                currentViewType = currentViewType,
                onViewTypeChanged = onViewTypeChanged
            )
        }
    )
}

/**
 * 视图类型选择器
 */
@Composable
private fun ViewTypeSelector(
    currentViewType: CalendarViewType,
    onViewTypeChanged: (CalendarViewType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = when (currentViewType) {
                    CalendarViewType.MONTH -> Icons.Outlined.CalendarMonth
                    CalendarViewType.WEEK -> Icons.Outlined.ViewWeek
                    CalendarViewType.DAY -> Icons.Outlined.ViewDay
                },
                contentDescription = "切换视图"
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CalendarViewType.values().forEach { viewType ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = when (viewType) {
                                    CalendarViewType.MONTH -> Icons.Outlined.CalendarMonth
                                    CalendarViewType.WEEK -> Icons.Outlined.ViewWeek
                                    CalendarViewType.DAY -> Icons.Outlined.ViewDay
                                },
                                contentDescription = null
                            )
                            Text(
                                when (viewType) {
                                    CalendarViewType.MONTH -> "月视图"
                                    CalendarViewType.WEEK -> "周视图"
                                    CalendarViewType.DAY -> "日视图"
                                }
                            )
                        }
                    },
                    onClick = {
                        onViewTypeChanged(viewType)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * 事件列表区域
 */
@Composable
private fun EventListSection(
    selectedDate: LocalDate,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "事件",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 占位内容
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无事件",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
