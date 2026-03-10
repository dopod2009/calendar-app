package com.calendar.core.domain.usecase

import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * 获取指定日期的事件用例
 */
class GetEventsByDateUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<CalendarEvent>> {
        return eventRepository.getByDateFlow(date)
    }
}
