package com.calendar.core.domain.usecase

import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.domain.repository.EventRepository
import javax.inject.Inject

/**
 * 添加事件用例
 */
class AddEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(event: CalendarEvent): Long {
        return eventRepository.insert(event)
    }
}
