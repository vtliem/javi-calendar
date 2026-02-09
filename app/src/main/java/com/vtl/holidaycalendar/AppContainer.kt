package com.vtl.holidaycalendar

import android.content.Context
import com.vtl.holidaycalendar.data.datasource.HolidayLocalDataSource
import com.vtl.holidaycalendar.data.datasource.HolidayRemoteDataSource
import com.vtl.holidaycalendar.data.datasource.OptionDataSource
import com.vtl.holidaycalendar.data.repository.HolidayRepositoryImpl
import com.vtl.holidaycalendar.domain.HolidayUseCase
import com.vtl.holidaycalendar.domain.OptionUseCase
import com.vtl.holidaycalendar.domain.repository.HolidayRepository

class AppContainer(private val context: Context) {
    private val holidayLocalDataSource by lazy { HolidayLocalDataSource(context) }
    private val holidayRemoteDataSource by lazy { HolidayRemoteDataSource() }
    
    val holidayRepository: HolidayRepository by lazy { 
        HolidayRepositoryImpl(
            localDataSource = holidayLocalDataSource,
            remoteDataSource = holidayRemoteDataSource
        )
    }

    val optionDataSource by lazy { OptionDataSource(context) }
    
    val holidayUseCase by lazy { HolidayUseCase(holidayRepository, context) }
    val optionUseCase by lazy { OptionUseCase(optionDataSource, context) }
}
