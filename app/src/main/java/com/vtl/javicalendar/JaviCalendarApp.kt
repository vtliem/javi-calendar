package com.vtl.javicalendar

import android.app.Application
import com.vtl.javicalendar.worker.DailyUpdateWorker
import com.vtl.javicalendar.worker.DailyUpdateWorker.Companion.nextMidnight

class JaviCalendarApp : Application() {

  // Manual Dependency Injection Container
  lateinit var container: AppContainer

  override fun onCreate() {
    super.onCreate()
    container = AppContainer(this)

    // Schedule daily update at 0:00 (Holidays + Widget)
    DailyUpdateWorker.schedule(this, nextMidnight())
  }
}
