package com.vtl.javicalendar.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.vtl.javicalendar.JaviCalendarApp
import com.vtl.javicalendar.widgets.WidgetManager
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

class DailyUpdateWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {
    Log.v(WORK_NAME, "doWork: runAttemptCount=$runAttemptCount")

    val result =
        try {
          val app = applicationContext as JaviCalendarApp
          val useCase = app.container.calendarSourcesUseCase

          // 1. Update the 'today' date in the UseCase
          useCase.updateToday()

          // 2. Trigger Widget Update with current cached data (today's date)
          val today = LocalDate.now()
          // Use only today check to ensure the date flip happens immediately
          val sources = useCase().first { it.today == today }
          WidgetManager.triggerUpdate(app, sources)

          Result.success()
        } catch (e: Exception) {
          Log.e(WORK_NAME, "doWork failed", e)
          if (runAttemptCount < 3) {
            Result.retry()
          } else {
            Result.failure()
          }
        }

    // Only schedule the next daily update if we're not retrying the current one.
    if (result != Result.retry()) {
      schedule(applicationContext, nextMidnight())
    }

    return result
  }

  companion object {
    private const val WORK_NAME = "DailyUpdateWorker"

    fun nextMidnight(): Duration {
      val now = LocalDateTime.now()
      val midnight = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.MIDNIGHT)
      return Duration.between(now, midnight)
    }

    fun schedule(
        context: Context,
        delay: Duration,
        policy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
    ) {
      Log.v(WORK_NAME, "schedule: delay=$delay, policy=$policy")
      val request =
          OneTimeWorkRequestBuilder<DailyUpdateWorker>()
              .setInitialDelay(delay)
              .addTag(WORK_NAME)
              .setBackoffCriteria(
                  BackoffPolicy.EXPONENTIAL,
                  WorkRequest.MIN_BACKOFF_MILLIS,
                  TimeUnit.MILLISECONDS,
              )
              .build()

      WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, policy, request)
    }
  }
}
