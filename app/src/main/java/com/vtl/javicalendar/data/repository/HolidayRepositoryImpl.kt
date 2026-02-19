package com.vtl.javicalendar.data.repository

import android.util.Log
import com.vtl.javicalendar.data.datasource.FetchHolidayError
import com.vtl.javicalendar.data.datasource.HolidayErrorType
import com.vtl.javicalendar.data.datasource.HolidayLocalDataSource
import com.vtl.javicalendar.data.datasource.HolidayRemoteDataSource
import com.vtl.javicalendar.domain.model.JapaneseHolidays
import com.vtl.javicalendar.domain.repository.HolidayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HolidayRepositoryImpl(
    private val localDataSource: HolidayLocalDataSource,
    private val remoteDataSource: HolidayRemoteDataSource,
) : HolidayRepository {

  companion object {
    private const val TAG = "HolidayRepositoryImpl"
  }

  override suspend fun getHolidays(): JapaneseHolidays =
      withContext(Dispatchers.Default) {
        val lastModified = localDataSource.loadLastModified()
        val newData =
            try {
              remoteDataSource.fetch(lastModified).also {
                if (it == null) {
                  Log.v(TAG, "Remote data unchanged")
                  localDataSource.saveSuccess()
                }
              }
            } catch (e: FetchHolidayError) {
              Log.w(TAG, "Fetch failed: ${e.message}", e)
              localDataSource.saveError(e.type, false)
              null
            } catch (e: Throwable) {
              Log.e(TAG, "Unexpected error during fetch", e)
              localDataSource.saveError(HolidayErrorType.Unknown, false)
              null
            }

        if (newData != null) {
          try {
            Log.v(TAG, "New data received: ${newData.lastModified}. Parsing...")
            val newHolidays =
                JapaneseHolidays.parseHolidays(
                        csv = newData.content,
                        lastModified = newData.lastModified,
                        lastSuccess = System.currentTimeMillis(),
                    )
                    .also {
                      if (it.years.isEmpty()) throw Exception("Japanese Holiday data is empty")
                    }
            localDataSource.saveData(newData)
            Log.v(TAG, "New data saved and returning")
            return@withContext newHolidays
          } catch (e: Throwable) {
            Log.e(TAG, "Failed to parse new data", e)
            localDataSource.saveError(HolidayErrorType.Parse, false)
          }
        }

        Log.v(TAG, "Attempting to load from local storage")
        localDataSource.loadData()?.let {
          try {
            val cachedHolidays =
                JapaneseHolidays.parseHolidays(
                    csv = it.content,
                    lastModified = it.lastModified,
                    lastSuccess = it.lastSuccess,
                    error = it.error,
                )
            Log.v(TAG, "Returning cached data (lastModified: ${it.lastModified})")
            return@withContext cachedHolidays
          } catch (e: Throwable) {
            Log.e(TAG, "Failed to parse cached data", e)
            localDataSource.saveError(HolidayErrorType.Parse, true)
            return@withContext JapaneseHolidays(error = HolidayErrorType.Parse)
          }
        }

        Log.w(TAG, "No data available locally or remotely. Returning empty holidays.")
        JapaneseHolidays()
      }
}
