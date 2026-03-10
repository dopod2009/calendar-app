package com.calendar.core.domain.usecase

import com.calendar.core.domain.repository.EventRepository
import javax.inject.Inject

/**
 * 删除事件用例
 */
class DeleteEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(eventId: Long) {
        eventRepository.softDelete(eventId)
    }
}
