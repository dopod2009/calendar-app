package com.calendar.feature.calendar.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt

/**
 * 日历视图类型
 */
enum class CalendarViewType {
    MONTH,
    WEEK,
    DAY
}

/**
 * 带手势滑动和动画效果的日历翻页组件
 */
@Composable
fun AnimatedCalendarPager(
    currentViewType: CalendarViewType,
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onYearMonthChanged: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
    eventDates: Set<LocalDate> = emptySet()
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    
    val animatedOffset by animateFloatAsState(
        targetValue = if (isDragging) dragOffset else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "animatedOffset"
    )
    
    Box(
        modifier = modifier
            .pointerInput(currentViewType, yearMonth) {
                detectHorizontalDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        if (dragOffset < -300f) {
                            // 左滑 - 下一个月/周/日
                            when (currentViewType) {
                                CalendarViewType.MONTH -> {
                                    onYearMonthChanged(yearMonth.plusMonths(1))
                                }
                                CalendarViewType.WEEK -> {
                                    onDateSelected(selectedDate.plusWeeks(1))
                                }
                                CalendarViewType.DAY -> {
                                    onDateSelected(selectedDate.plusDays(1))
                                }
                            }
                        } else if (dragOffset > 300f) {
                            // 右滑 - 上一个月/周/日
                            when (currentViewType) {
                                CalendarViewType.MONTH -> {
                                    onYearMonthChanged(yearMonth.minusMonths(1))
                                }
                                CalendarViewType.WEEK -> {
                                    onDateSelected(selectedDate.minusWeeks(1))
                                }
                                CalendarViewType.DAY -> {
                                    onDateSelected(selectedDate.minusDays(1))
                                }
                            }
                        }
                        dragOffset = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier.offset { IntOffset(animatedOffset.roundToInt(), 0) }
        ) {
            when (currentViewType) {
                CalendarViewType.MONTH -> {
                    MonthCalendarView(
                        yearMonth = yearMonth,
                        selectedDate = selectedDate,
                        onDateSelected = onDateSelected,
                        eventDates = eventDates
                    )
                }
                CalendarViewType.WEEK -> {
                    WeekCalendarView(
                        selectedDate = selectedDate,
                        onDateSelected = onDateSelected,
                        eventDates = eventDates
                    )
                }
                CalendarViewType.DAY -> {
                    // DayView在主界面单独展示
                    Spacer(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

/**
 * 视图切换动画组件
 */
@Composable
fun ViewTypeTransition(
    currentViewType: CalendarViewType,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedContent(
        targetState = currentViewType,
        modifier = modifier,
        transitionSpec = {
            slideInVertically(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                initialOffsetY = { if (targetState.ordinal > initialState.ordinal) it else -it }
            ) togetherWith slideOutVertically(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                targetOffsetY = { if (targetState.ordinal > initialState.ordinal) -it else it }
            )
        },
        label = "viewTypeTransition"
    ) {
        content()
    }
}
