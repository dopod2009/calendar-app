package com.calendar.core.data.model

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

/**
 * Room类型转换器
 * 用于处理Java 8时间API和自定义枚举
 */
class Converters {
    
    // LocalDate转换
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
    
    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? {
        return epochDay?.let { LocalDate.ofEpochDay(it) }
    }
    
    // LocalTime转换
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): Int? {
        return time?.toSecondOfDay()
    }
    
    @TypeConverter
    fun toLocalTime(seconds: Int?): LocalTime? {
        return seconds?.let { LocalTime.ofSecondOfDay(it.toLong()) }
    }
    
    // LocalDateTime转换
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): Long? {
        return dateTime?.atZone(ZoneOffset.UTC)?.toEpochSecond()
    }
    
    @TypeConverter
    fun toLocalDateTime(epochSecond: Long?): LocalDateTime? {
        return epochSecond?.let { 
            LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) 
        }
    }
    
    // SyncStatus转换
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toSyncStatus(name: String): SyncStatus {
        return SyncStatus.valueOf(name)
    }
}
