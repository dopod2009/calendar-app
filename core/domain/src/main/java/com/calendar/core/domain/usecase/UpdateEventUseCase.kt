package com.calendar.core.domain.usecase

import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.domain.repository.EventRepository
import javax.inject.Inject

/**
 * 更新事件用例
 */
class UpdateEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(event: CalendarEvent) {
        eventRepository.update(event)
    }
}
