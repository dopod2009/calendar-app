package com.calendar.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CalendarApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Application initialization
    }
}
