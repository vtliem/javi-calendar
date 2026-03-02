package com.vtl.javicalendar

import android.app.Application
import androidx.work.ExistingWorkPolicy
import com.vtl.javicalendar.worker.DailyUpdateWorker
import com.vtl.javicalendar.worker.DailyUpdateWorker.Companion.nextMidnight
import com.vtl.javicalendar.worker.HolidaySyncWorker

class JaviCalendarApp : Application() {

  // Manual Dependency Injection Container
  lateinit var container: AppContainer

  override fun onCreate() {
    super.onCreate()
    container = AppContainer(this)

    // Schedule daily update at 0:00 (Widget Date Flip)
    // Use KEEP to ensure we don't reschedule today's update to tomorrow if the app is opened just
    // after midnight
    DailyUpdateWorker.schedule(this, nextMidnight(), ExistingWorkPolicy.KEEP)

    // Setup periodic holiday sync (Independent of date change)
    HolidaySyncWorker.setupPeriodicWork(this)
  }
}
