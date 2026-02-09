package com.vtl.holidaycalendar

import android.app.Application
import com.vtl.holidaycalendar.worker.DailyUpdateWorker

class HolidayCalendarApp : Application() {

    // Manual Dependency Injection Container
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        
        // Schedule daily update at 0:00 (Holidays + Widget)
        DailyUpdateWorker.schedule(this)
    }
}
