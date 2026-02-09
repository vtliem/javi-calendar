package com.vtl.holidaycalendar.data.repository

import com.vtl.holidaycalendar.data.datasource.HolidayLocalDataSource
import com.vtl.holidaycalendar.data.datasource.HolidayRemoteDataSource
import com.vtl.holidaycalendar.domain.model.JapaneseHolidays
import com.vtl.holidaycalendar.domain.repository.HolidayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HolidayRepositoryImpl(
    private val localDataSource: HolidayLocalDataSource,
    private val remoteDataSource: HolidayRemoteDataSource
) : HolidayRepository {

    override suspend fun getHolidays(): JapaneseHolidays = withContext(Dispatchers.Default) {
        // 1. Load data from local
        val localData = localDataSource.loadData()
        
        // 2. Fetch from remote (checks ETag internally)
        val newData = remoteDataSource.fetch(localData?.etag)
        
        val finalContent = if (newData != null) {
            // Data changed, save to local
            localDataSource.saveData(newData)
            newData.content
        } else {
            // No changes or fetch failed, use local data
            localData?.content ?: ""
        }

        JapaneseHolidays.parseHolidays(finalContent)
    }
}
