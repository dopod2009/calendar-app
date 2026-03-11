package com.calendar.core.data.repository

import com.calendar.core.data.model.EventDao
import com.calendar.core.data.model.EventEntity
import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 事件Repository实现
 */
@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao
) : EventRepository {

    override suspend fun insert(event: CalendarEvent): Long {
        return eventDao.insert(EventEntity.fromDomainModel(event))
    }

    override suspend fun update(event: CalendarEvent) {
        eventDao.update(EventEntity.fromDomainModel(event))
    }

    override suspend fun delete(event: CalendarEvent) {
        eventDao.delete(EventEntity.fromDomainModel(event))
    }

    override suspend fun softDelete(eventId: Long) {
        eventDao.softDelete(eventId)
    }

    override suspend fun getById(eventId: Long): CalendarEvent? {
        return eventDao.getById(eventId)?.toDomainModel()
    }

    override fun getByIdFlow(eventId: Long): Flow<CalendarEvent?> {
        return eventDao.getByIdFlow(eventId).map { it?.toDomainModel() }
    }

    override suspend fun getByDate(date: LocalDate): List<CalendarEvent> {
        return eventDao.getByDate(date.toEpochDay()).map { it.toDomainModel() }
    }

    override fun getByDateFlow(date: LocalDate): Flow<List<CalendarEvent>> {
        return eventDao.getByDateFlow(date.toEpochDay()).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getByDateRange(startDate: LocalDate, endDate: LocalDate): List<CalendarEvent> {
        return eventDao.getByDateRange(startDate.toEpochDay(), endDate.toEpochDay()).map { it.toDomainModel() }
    }

    override fun getByDateRangeFlow(startDate: LocalDate, endDate: LocalDate): Flow<List<CalendarEvent>> {
        return eventDao.getByDateRangeFlow(startDate.toEpochDay(), endDate.toEpochDay()).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun search(keyword: String): List<CalendarEvent> {
        return eventDao.search(keyword).map { it.toDomainModel() }
    }

    override fun searchFlow(keyword: String): Flow<List<CalendarEvent>> {
        return eventDao.searchFlow(keyword).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAllFlow(): Flow<List<CalendarEvent>> {
        return eventDao.getAllFlow().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getPendingSync(): List<CalendarEvent> {
        return eventDao.getPendingSync().map { it.toDomainModel() }
    }

    override suspend fun getConflicts(): List<CalendarEvent> {
        return eventDao.getConflicts().map { it.toDomainModel() }
    }

    override suspend fun updateSyncStatus(eventId: Long, isSynced: Boolean) {
        val status = if (isSynced) com.calendar.core.data.model.SyncStatus.SYNCED 
                     else com.calendar.core.data.model.SyncStatus.PENDING
        eventDao.updateSyncStatus(eventId, status)
    }

    override suspend fun getDatesWithEvents(startDate: LocalDate, endDate: LocalDate): Set<LocalDate> {
        return eventDao.getDatesWithEvents(startDate.toEpochDay(), endDate.toEpochDay())
            .map { LocalDate.ofEpochDay(it) }
            .toSet()
    }

    override suspend fun getCountByDate(date: LocalDate): Int {
        return eventDao.getCountByDate(date.toEpochDay())
    }
}
