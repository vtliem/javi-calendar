package com.vtl.holidaycalendar.domain.repository

import com.vtl.holidaycalendar.domain.model.JapaneseHolidays

interface HolidayRepository {
    suspend fun getHolidays(): JapaneseHolidays
    suspend fun refreshHolidaysIfNeeded()
}
