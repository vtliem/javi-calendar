package com.vtl.javicalendar.domain

import android.content.Context
import com.vtl.javicalendar.data.datasource.HolidayLocalDataSource
import com.vtl.javicalendar.data.datasource.HolidayRemoteDataSource
import com.vtl.javicalendar.data.datasource.OptionDataSource
import com.vtl.javicalendar.data.repository.HolidayRepositoryImpl
import com.vtl.javicalendar.presentation.model.CalendarSources
import com.vtl.javicalendar.presentation.model.Option
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.getAndUpdate

class CalendarSourcesUseCase(
    private val holidayUseCase: HolidayUseCase,
    private val optionUseCase: OptionUseCase,
) {
  companion object {
    fun create(context: Context) =
        CalendarSourcesUseCase(
            holidayUseCase =
                HolidayUseCase(
                    HolidayRepositoryImpl(
                        localDataSource = HolidayLocalDataSource(context),
                        remoteDataSource = HolidayRemoteDataSource(),
                    )
                ),
            optionUseCase = OptionUseCase(OptionDataSource(context)),
        )
  }

  private val _today = MutableStateFlow(LocalDate.now())
  private val sources =
      combine(holidayUseCase(), optionUseCase(), _today) { holidays, option, today ->
            CalendarSources(holidays, option, today)
          }
          .distinctUntilChanged()

  operator fun invoke() = sources

  suspend fun refresh(): Boolean {
    val today = LocalDate.now()
    val todayUpdated = _today.getAndUpdate { today } != today
    return holidayUseCase.refresh() || todayUpdated
  }

  fun updateOption(newOption: Option) = optionUseCase.updateOption(newOption)
}
