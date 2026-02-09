package com.vtl.holidaycalendar

import android.app.Application

class HolidayCalendarApp : Application() {

    // Manual Dependency Injection Container
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

