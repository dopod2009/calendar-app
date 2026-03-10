package com.calendar.core.data.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * 日历应用数据库
 */
@Database(
    entities = [
        EventEntity::class,
        EventCategoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CalendarDatabase : RoomDatabase() {
    
    abstract fun eventDao(): EventDao
    abstract fun eventCategoryDao(): EventCategoryDao
}
