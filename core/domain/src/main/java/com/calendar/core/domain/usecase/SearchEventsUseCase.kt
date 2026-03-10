package com.calendar.core.domain.usecase

import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 搜索事件用例
 */
class SearchEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    operator fun invoke(keyword: String): Flow<List<CalendarEvent>> {
        return eventRepository.searchFlow(keyword)
    }
}
