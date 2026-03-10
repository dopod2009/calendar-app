package com.calendar.core.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * 事件分类数据访问对象
 */
@Dao
interface EventCategoryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: EventCategoryEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<EventCategoryEntity>)
    
    @Update
    suspend fun update(category: EventCategoryEntity)
    
    @Delete
    suspend fun delete(category: EventCategoryEntity)
    
    @Query("DELETE FROM event_categories WHERE id = :categoryId")
    suspend fun deleteById(categoryId: Long)
    
    @Query("SELECT * FROM event_categories WHERE id = :categoryId")
    suspend fun getById(categoryId: Long): EventCategoryEntity?
    
    @Query("SELECT * FROM event_categories ORDER BY sortOrder ASC")
    suspend fun getAll(): List<EventCategoryEntity>
    
    @Query("SELECT * FROM event_categories ORDER BY sortOrder ASC")
    fun getAllFlow(): Flow<List<EventCategoryEntity>>
    
    @Query("SELECT * FROM event_categories WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultCategory(): EventCategoryEntity?
}
