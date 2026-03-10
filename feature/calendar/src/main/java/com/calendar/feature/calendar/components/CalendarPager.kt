package com.calendar.feature.calendar.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.YearMonth

/**
 * 无限滚动的月历分页组件
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfiniteMonthPager(
    initialYearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onYearMonthChanged: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
    eventDates: Set<LocalDate> = emptySet()
) {
    // 使用中间值作为起始位置，向前后扩展
    val initialPage = Int.MAX_VALUE / 2
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialPage)
    
    // 监听当前显示的月份
    LaunchedEffect(listState.firstVisibleItemIndex) {
        val offset = listState.firstVisibleItemIndex - initialPage
        val currentMonth = initialYearMonth.plusMonths(offset.toLong())
        onYearMonthChanged(currentMonth)
    }
    
    LazyRow(
        state = listState,
        modifier = modifier,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(
            count = Int.MAX_VALUE,
            key = { page ->
                val offset = page - initialPage
                initialYearMonth.plusMonths(offset.toLong())
            }
        ) { page ->
            val offset = page - initialPage
            val yearMonth = initialYearMonth.plusMonths(offset.toLong())
            
            Box(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .fillMaxHeight()
            ) {
                MonthCalendarView(
                    yearMonth = yearMonth,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected,
                    eventDates = eventDates
                )
            }
        }
    }
}

/**
 * 平滑过渡的视图切换容器
 */
@Composable
fun SmoothTransitionContainer(
    currentViewType: CalendarViewType,
    modifier: Modifier = Modifier,
    content: @Composable (CalendarViewType) -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(200)) + slideInVertically(
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            initialOffsetY = { -it / 10 }
        ),
        exit = fadeOut(animationSpec = tween(200)) + slideOutVertically(
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            targetOffsetY = { it / 10 }
        ),
        modifier = modifier
    ) {
        content(currentViewType)
    }
}
