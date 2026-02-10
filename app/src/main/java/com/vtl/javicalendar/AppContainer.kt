package com.vtl.javicalendar

import android.content.Context
import com.vtl.javicalendar.domain.CalendarSourcesUseCase

class AppContainer(private val context: Context) {
    val calendarSourcesUseCase by lazy { CalendarSourcesUseCase.create(context) }
}
