package com.calendar.core.data.model

import androidx.room.*
import com.calendar.core.domain.model.ReminderStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE eventId = :eventId ORDER BY reminderTime ASC")
    fun getRemindersByEventId(eventId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE reminderTime <= :time AND status = :status ORDER BY reminderTime ASC")
    suspend fun getPendingRemindersBefore(time: LocalDateTime, status: ReminderStatus = ReminderStatus.PENDING): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE status = :status ORDER BY reminderTime ASC")
    fun getRemindersByStatus(status: ReminderStatus): Flow<List<ReminderEntity>>

    @Query("SELECT COUNT(*) FROM reminders WHERE eventId = :eventId AND status = :status")
    suspend fun countByEventIdAndStatus(eventId: Long, status: ReminderStatus): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE eventId = :eventId")
    suspend fun deleteByEventId(eventId: Long)

    @Query("UPDATE reminders SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: ReminderStatus)

    @Transaction
    @Query("SELECT * FROM reminders WHERE reminderTime BETWEEN :startTime AND :endTime AND status = :status")
    suspend fun getRemindersInRange(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        status: ReminderStatus = ReminderStatus.PENDING
    ): List<ReminderEntity>
}
