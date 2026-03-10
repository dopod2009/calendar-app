package com.calendar.core.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.calendar.core.domain.model.ReminderStatus
import java.time.LocalDateTime

/**
 * 提醒实体（Room数据库表）
 */
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["eventId"]),
        Index(value = ["reminderTime"]),
        Index(value = ["status"])
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventId: Long,
    val reminderTime: LocalDateTime,
    val message: String? = null,
    val status: ReminderStatus = ReminderStatus.PENDING,
    val notificationId: Int? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
