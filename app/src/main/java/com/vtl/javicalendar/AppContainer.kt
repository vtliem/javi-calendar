package com.vtl.javicalendar

import android.content.Context
import com.vtl.javicalendar.data.datasource.HolidayLocalDataSource
import com.vtl.javicalendar.data.datasource.HolidayRemoteDataSource
import com.vtl.javicalendar.data.datasource.OptionDataSource
import com.vtl.javicalendar.data.repository.HolidayRepositoryImpl
import com.vtl.javicalendar.domain.HolidayUseCase
import com.vtl.javicalendar.domain.OptionUseCase
import com.vtl.javicalendar.domain.repository.HolidayRepository

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
