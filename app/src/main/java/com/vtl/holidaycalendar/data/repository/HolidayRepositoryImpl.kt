package com.vtl.holidaycalendar.data.repository

import com.vtl.holidaycalendar.data.datasource.HolidayDataSource
import com.vtl.holidaycalendar.domain.model.JapaneseHolidays
import com.vtl.holidaycalendar.domain.repository.HolidayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HolidayRepositoryImpl(
    private val dataSource: HolidayDataSource
) : HolidayRepository {

    override suspend fun getHolidays(): JapaneseHolidays = withContext(Dispatchers.Default) {
        val csvData = dataSource.getFromCache() ?: ""
        JapaneseHolidays.parseHolidays(csvData)
    }

    override suspend fun refreshHolidaysIfNeeded() {
        if (dataSource.shouldRefresh()) {
            val newData = dataSource.fetchHolidays()
            if (newData != null) {
                dataSource.saveToCache(newData)
            }
        }
    }
}
