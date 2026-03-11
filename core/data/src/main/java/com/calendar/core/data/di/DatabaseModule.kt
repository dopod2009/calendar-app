package com.calendar.core.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.calendar.core.data.model.CalendarDatabase
import com.calendar.core.data.model.EventCategoryDao
import com.calendar.core.data.model.EventDao
import com.calendar.core.data.repository.AuthRepositoryImpl
import com.calendar.core.data.repository.SyncRepositoryImpl
import com.calendar.core.domain.repository.EventRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    private const val PREFS_NAME = "calendar_prefs"
    
    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    @Provides
    @Singleton
    fun provideCalendarDatabase(
        @ApplicationContext context: Context
    ): CalendarDatabase {
        return Room.databaseBuilder(
            context,
            CalendarDatabase::class.java,
            "calendar_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideEventDao(database: CalendarDatabase): EventDao {
        return database.eventDao()
    }
    
    @Provides
    fun provideEventCategoryDao(database: CalendarDatabase): EventCategoryDao {
        return database.eventCategoryDao()
    }
}

/**
 * Repository绑定模块
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindEventRepository(
        eventRepositoryImpl: com.calendar.core.data.repository.EventRepositoryImpl
    ): EventRepository
}
