package com.vtl.javicalendar.presentation.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vtl.javicalendar.JaviCalendarApp
import com.vtl.javicalendar.domain.CalendarFactory
import com.vtl.javicalendar.domain.CalendarSourcesUseCase
import com.vtl.javicalendar.domain.model.JapaneseHolidays
import com.vtl.javicalendar.presentation.model.CalendarSources
import com.vtl.javicalendar.presentation.model.DateInfo
import com.vtl.javicalendar.presentation.model.MonthInfo
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.widgets.WidgetManager
import java.time.LocalDate
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ViewMode {
  CALENDAR,
  YEAR_SELECT,
  MONTH_SELECT,
  SETTINGS,
}

data class HomeUiState(
    val holidays: JapaneseHolidays = JapaneseHolidays(emptyMap()),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonthInfo: MonthInfo? = null,
    val selectedDateInfo: DateInfo? = null,
    val viewMode: ViewMode = ViewMode.CALENDAR,
    val scrollToDate: LocalDate? = null,
    val option: Option = Option(),
    val today: LocalDate = LocalDate.now(),
)

data class InternalUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val viewMode: ViewMode = ViewMode.CALENDAR,
    val scrollToDate: LocalDate? = null,
    val today: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class CalendarViewModel(
    private val calendarSourcesUseCase: CalendarSourcesUseCase,
    application: Application,
) : AndroidViewModel(application) {

  private val _internalState = MutableStateFlow(InternalUiState())

  val uiState: StateFlow<HomeUiState> =
      combine(calendarSourcesUseCase(), _internalState) { sources, internal ->
            val state =
                HomeUiState(
                    holidays = sources.holidays,
                    option = sources.option,
                    selectedDate = internal.selectedDate,
                    viewMode = internal.viewMode,
                    scrollToDate = internal.scrollToDate,
                    today = internal.today,
                    isLoading = internal.isLoading,
                    error = internal.error,
                )
            updateCalculatedData(state)
          }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5000),
              initialValue = HomeUiState(),
          )

  init {
    syncJapaneseHolidays()
  }

  fun syncJapaneseHolidays() {
    viewModelScope.launch {
      calendarSourcesUseCase().collectLatest { sources -> refreshWidget(sources) }
      _internalState.update { it.copy(isLoading = true) }
      try {
        calendarSourcesUseCase.refresh()
        _internalState.update { it.copy(isLoading = false) }
      } catch (e: Exception) {
        _internalState.update { it.copy(isLoading = false, error = e.message) }
      }
    }
  }

  private fun updateCalculatedData(state: HomeUiState): HomeUiState {
    val selected = state.selectedDate
    val monthInfo =
        CalendarFactory.createMonthInfo(
            state.option.sundayFirst,
            selected.year,
            selected.monthValue,
            state.holidays,
            selectedDate = selected,
            today = state.today,
        )
    val dateInfo = monthInfo.getDate(selected)
    return state.copy(currentMonthInfo = monthInfo, selectedDateInfo = dateInfo)
  }

  fun selectDate(date: LocalDate) {
    _internalState.update { it.copy(selectedDate = date, scrollToDate = null) }
  }

  fun changeYear(year: Int) {
    val targetDate = uiState.value.selectedDate.withYear(year)
    _internalState.update { it.copy(viewMode = ViewMode.CALENDAR, scrollToDate = targetDate) }
  }

  fun changeMonth(month: Int) {
    val targetDate = uiState.value.selectedDate.withMonth(month)
    _internalState.update { it.copy(viewMode = ViewMode.CALENDAR, scrollToDate = targetDate) }
  }

  fun goToToday() {
    val today = LocalDate.now()
    _internalState.update {
      it.copy(
          today = today,
          selectedDate = today,
          scrollToDate = today,
          viewMode = ViewMode.CALENDAR,
      )
    }
  }

  fun scrollToSelectedDate() {
    _internalState.update { it.copy(scrollToDate = it.selectedDate) }
  }

  fun setViewMode(mode: ViewMode) {
    _internalState.update { it.copy(viewMode = mode) }
  }

  fun onScrollHandled() {
    _internalState.update { it.copy(scrollToDate = null) }
  }

  fun updateOption(newOption: Option) {
    calendarSourcesUseCase.updateOption(newOption)
  }

  private suspend fun refreshWidget(sources: CalendarSources) {
    Log.v("CalendarViewModel", "refreshWidget :${sources.today}")
    WidgetManager.triggerUpdate(getApplication(), sources)
  }

  companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val application =
            (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JaviCalendarApp)
        CalendarViewModel(
            calendarSourcesUseCase = application.container.calendarSourcesUseCase,
            application,
        )
      }
    }
  }
}
