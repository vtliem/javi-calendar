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

  override suspend fun getHolidays(): JapaneseHolidays =
      withContext(Dispatchers.Default) {
        // 1. Load lastModified from local
        val lastModified = localDataSource.loadLastModified()

        // 2. Fetch from remote
        val newData =
            try {
              remoteDataSource.fetch(lastModified).also {
                if (it == null) {
                  // unchanged
                  localDataSource.saveSuccess()
                }
              }
            } catch (e: FetchHolidayError) {
              Log.w("HolidayRepositoryImpl", e.javaClass.simpleName, e)
              localDataSource.saveError(e.type, false)
              null
            }

        if (newData != null) {
          // has changes
          try {
            val newHolidays =
                JapaneseHolidays.parseHolidays(
                    csv = newData.content,
                    lastModified = newData.lastModified,
                    lastSuccess = System.currentTimeMillis(),
                )
            // 3. Save to local
            localDataSource.saveData(newData)
            return@withContext newHolidays
          } catch (e: Throwable) {
            Log.e("HolidayRepositoryImpl", "parseHolidays failed", e)
            localDataSource.saveError(HolidayErrorType.Parse, false)
          }
        }
        // load from local
        localDataSource.loadData()?.let {
          try {
            return@withContext JapaneseHolidays.parseHolidays(
                csv = it.content,
                lastModified = it.lastModified,
                lastSuccess = it.lastSuccess,
                error = it.error,
            )
          } catch (e: Throwable) {
            Log.e("HolidayRepositoryImpl", "parseHolidays old data failed", e)
            localDataSource.saveError(HolidayErrorType.Parse, true)
            return@withContext JapaneseHolidays(error = HolidayErrorType.Parse)
          }
        }
        // default
        JapaneseHolidays()
      }
}
