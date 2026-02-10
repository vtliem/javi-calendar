package com.vtl.javicalendar.worker

import android.content.Context
import androidx.work.*
import com.vtl.javicalendar.JaviCalendarApp
import com.vtl.javicalendar.widgets.WidgetManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

class DailyUpdateWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {
    val app = applicationContext as JaviCalendarApp
    val calendarSourcesUseCase = app.container.calendarSourcesUseCase
    if (!calendarSourcesUseCase.refresh()) {
      val sources = calendarSourcesUseCase().first()
      WidgetManager.triggerUpdate(app, sources)
    }
    return Result.success()
  }

  companion object {
    private const val WORK_NAME = "DailyUpdateWorker"

    fun schedule(context: Context) {
      val workManager = WorkManager.getInstance(context)

      // Calculate delay until next midnight
      val now = LocalDateTime.now()
      val nextMidnight = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.MIDNIGHT)
      val delay = Duration.between(now, nextMidnight).toMinutes()

      val request =
          PeriodicWorkRequestBuilder<DailyUpdateWorker>(24, TimeUnit.HOURS)
              .setInitialDelay(delay, TimeUnit.MINUTES)
              .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
              .build()

      workManager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
    }
  }
}
