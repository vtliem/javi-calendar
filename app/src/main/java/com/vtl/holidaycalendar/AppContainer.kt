package com.vtl.holidaycalendar

import android.content.Context
import com.vtl.holidaycalendar.data.datasource.HolidayDataSource
import com.vtl.holidaycalendar.data.repository.HolidayRepositoryImpl
import com.vtl.holidaycalendar.domain.repository.HolidayRepository

class AppContainer(private val context: Context) {
    private val holidayDataSource by lazy { HolidayDataSource(context) }
    
    val holidayRepository: HolidayRepository by lazy { 
        HolidayRepositoryImpl(holidayDataSource) 
    }
}
