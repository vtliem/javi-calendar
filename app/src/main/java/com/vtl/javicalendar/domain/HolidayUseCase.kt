package com.vtl.javicalendar.domain

import com.vtl.javicalendar.domain.model.JapaneseHolidays
import com.vtl.javicalendar.domain.repository.HolidayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

class HolidayUseCase(
    private val repository: HolidayRepository,
) {
    companion object{
        private const val REFRESH_INTERVAL = 1000 * 60 * 60 * 24
    }
    private val _holidays = MutableStateFlow(JapaneseHolidays(emptyMap()))
    private val holidays: StateFlow<JapaneseHolidays> = _holidays.asStateFlow()
    operator fun invoke() = holidays

    @OptIn(ExperimentalAtomicApi::class)
    private val lastUpdated = AtomicLong(0L)

    @OptIn(ExperimentalAtomicApi::class)
    suspend fun refresh() = if (System.currentTimeMillis() - lastUpdated.load() < REFRESH_INTERVAL) {
        false
    } else repository.getHolidays().let { newData ->
        _holidays.getAndUpdate { newData } != newData
    }.also {
        if (it) {
            lastUpdated.store(System.currentTimeMillis())
        }
    }
}
