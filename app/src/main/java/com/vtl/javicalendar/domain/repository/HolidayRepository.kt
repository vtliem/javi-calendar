package com.vtl.javicalendar.domain.repository

import com.vtl.javicalendar.domain.model.JapaneseHolidays

interface HolidayRepository {
  suspend fun getHolidays(): JapaneseHolidays
}
