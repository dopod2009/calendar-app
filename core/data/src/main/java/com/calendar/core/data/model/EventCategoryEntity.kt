package com.calendar.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 事件分类实体
 */
@Entity(tableName = "event_categories")
data class EventCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val color: String,
    val icon: String = "",
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
