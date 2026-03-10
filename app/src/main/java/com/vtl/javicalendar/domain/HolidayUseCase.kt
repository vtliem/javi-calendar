package com.vtl.javicalendar.domain

import com.vtl.javicalendar.domain.model.JapaneseHolidays
import com.vtl.javicalendar.domain.repository.HolidayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.getAndUpdate

class HolidayUseCase(
    private val repository: HolidayRepository,
) {
  companion object {
    private const val REFRESH_INTERVAL = 1000 * 60 * 60 * 24
  }

  private val _holidays = MutableStateFlow(JapaneseHolidays())

  private val holidays: Flow<JapaneseHolidays> by lazy {
    flow {
      if (_holidays.value.isEmpty) {
        _holidays.value = repository.getHolidays(refresh = false)
      }
      emitAll(_holidays.asStateFlow())
    }
  }

  operator fun invoke(): Flow<JapaneseHolidays> = holidays

  private val lastUpdated
    get() = _holidays.value.lastSuccess

  suspend fun refresh() =
      if (_holidays.value.error == null && System.currentTimeMillis() - lastUpdated < REFRESH_INTERVAL) {
        false
      } else {
        repository.getHolidays(refresh = true).let { newData ->
          _holidays.getAndUpdate { newData } != newData
        }
      }
}
