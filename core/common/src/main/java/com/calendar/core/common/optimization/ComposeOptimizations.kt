package com.calendar.core.common.optimization

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/**
 * Compose性能优化工具
 */
object ComposeOptimizations {

    /**
     * 懒加载状态追踪
     */
    @Composable
    fun LazyListState.isScrollingUp(): Boolean {
        var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
        var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }

        return remember(this) {
            derivedStateOf {
                if (previousIndex != firstVisibleItemIndex) {
                    previousIndex > firstVisibleItemIndex
                } else {
                    previousScrollOffset >= firstVisibleItemScrollOffset
                }.also {
                    previousIndex = firstVisibleItemIndex
                    previousScrollOffset = firstVisibleItemScrollOffset
                }
            }
        }.value
    }

    /**
     * 滚动到底部监听
     */
    @Composable
    fun LazyListState.OnReachedBottom(
        buffer: Int = 3,
        onLoadMore: () -> Unit
    ) {
        val shouldLoadMore by remember {
            derivedStateOf {
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = layoutInfo.totalItemsCount
                lastVisibleItem >= totalItems - buffer
            }
        }

        LaunchedEffect(shouldLoadMore) {
            if (shouldLoadMore) {
                onLoadMore()
            }
        }
    }

    /**
     * 防抖动状态
     */
    @Composable
    fun <T> T.debounce(
        delayMs: Long = 300L
    ): T {
        var debouncedValue by remember { mutableStateOf(this) }

        LaunchedEffect(this) {
            kotlinx.coroutines.delay(delayMs)
            debouncedValue = this@debounce
        }

        return debouncedValue
    }

    /**
     * 节流状态
     */
    @Composable
    fun <T> T.throttle(
        intervalMs: Long = 1000L
    ): T {
        var lastEmissionTime by remember { mutableStateOf(0L) }
        var throttledValue by remember { mutableStateOf(this) }

        LaunchedEffect(this) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastEmissionTime >= intervalMs) {
                throttledValue = this@throttle
                lastEmissionTime = currentTime
            }
        }

        return throttledValue
    }
}

/**
 * 列表项key生成器
 */
object ItemKeyGenerator {
    private var counter = 0L

    fun generateKey(): String {
        return "item_${System.currentTimeMillis()}_${counter++}"
    }
}
