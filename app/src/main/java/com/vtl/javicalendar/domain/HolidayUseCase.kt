package com.vtl.javicalendar.domain

import com.vtl.javicalendar.domain.model.JapaneseHolidays
import com.vtl.javicalendar.domain.repository.HolidayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate

class HolidayUseCase(
    private val repository: HolidayRepository,
) {
  companion object {
    private const val REFRESH_INTERVAL = 1000 * 60 * 60 * 24
  }

  private val _holidays = MutableStateFlow(JapaneseHolidays())
  private val holidays: StateFlow<JapaneseHolidays> = _holidays.asStateFlow()

  operator fun invoke() = holidays

  private val lastUpdated
    get() = holidays.value.lastSuccess

  suspend fun refresh() =
      if (System.currentTimeMillis() - lastUpdated < REFRESH_INTERVAL) {
        false
      } else
          repository.getHolidays().let { newData -> _holidays.getAndUpdate { newData } != newData }
}
