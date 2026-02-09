package com.vtl.javicalendar.domain

import android.content.Context
import com.vtl.javicalendar.domain.model.JapaneseHolidays
import com.vtl.javicalendar.domain.repository.HolidayRepository
import com.vtl.javicalendar.widgets.WidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

class HolidayUseCase(
    private val repository: HolidayRepository,
    private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val _holidays = MutableStateFlow(JapaneseHolidays(emptyMap()))
    val holidays: StateFlow<JapaneseHolidays> = _holidays.asStateFlow()

    suspend fun refreshHolidays() {
        val newData = repository.getHolidays()
        val oldData = _holidays.getAndUpdate { newData }
        
        if (oldData != newData) {
            scope.launch {
                WidgetManager.triggerUpdate(context)
            }
        }
    }
}
