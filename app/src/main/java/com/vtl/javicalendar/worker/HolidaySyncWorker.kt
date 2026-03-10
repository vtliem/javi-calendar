package com.vtl.javicalendar.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.vtl.javicalendar.JaviCalendarApp
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

class HolidaySyncWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {
    Log.v(WORK_NAME, "doWork: runAttemptCount=$runAttemptCount")

    return try {
      val app = applicationContext as JaviCalendarApp
      val useCase = app.container.calendarSourcesUseCase
      useCase.refreshHolidays()
      Result.success()
    } catch (e: Exception) {
      Log.e(WORK_NAME, "doWork failed", e)
      if (runAttemptCount < 3) {
        Result.retry()
      } else {
        Result.failure()
      }
    }
  }

  companion object {
    private const val WORK_NAME = "HolidaySyncWorker"

    fun setupPeriodicWork(context: Context) {
      Log.v(WORK_NAME, "setupPeriodicWork")
      val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

      val request =
          PeriodicWorkRequestBuilder<HolidaySyncWorker>(24, TimeUnit.HOURS)
              .setConstraints(constraints)
              .addTag(WORK_NAME)
              .setBackoffCriteria(
                  BackoffPolicy.EXPONENTIAL,
                  WorkRequest.MIN_BACKOFF_MILLIS,
                  TimeUnit.MILLISECONDS,
              )
              .build()

      WorkManager.getInstance(context)
          .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }
  }
}
