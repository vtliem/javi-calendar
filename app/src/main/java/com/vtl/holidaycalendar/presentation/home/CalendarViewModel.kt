package com.vtl.holidaycalendar.presentation.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vtl.holidaycalendar.HolidayCalendarApp
import com.vtl.holidaycalendar.domain.CalendarFactory
import com.vtl.holidaycalendar.domain.HolidayUseCase
import com.vtl.holidaycalendar.domain.OptionUseCase
import com.vtl.holidaycalendar.domain.model.JapaneseHolidays
import com.vtl.holidaycalendar.presentation.model.DateInfo
import com.vtl.holidaycalendar.presentation.model.MonthInfo
import com.vtl.holidaycalendar.presentation.model.Option
import com.vtl.holidaycalendar.widgets.WidgetManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class ViewMode {
    CALENDAR, YEAR_SELECT, MONTH_SELECT, SETTINGS
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
    val today: LocalDate = LocalDate.now()
)

class CalendarViewModel(
    private val holidayUseCase: HolidayUseCase,
    private val optionUseCase: OptionUseCase,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Observe UseCase flows and update UI state
        combine(
            holidayUseCase.holidays,
            optionUseCase.option
        ) { holidays, option ->
            _uiState.update { state ->
                val newState = state.copy(holidays = holidays, option = option)
                updateCalculatedData(newState)
            }
        }.launchIn(viewModelScope)

        // Initial load
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                holidayUseCase.refreshHolidays()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun updateCalculatedData(state: HomeUiState): HomeUiState {
        val selected = state.selectedDate
        val monthInfo = CalendarFactory.createMonthInfo(
            selected.year,
            selected.monthValue,
            state.holidays,
            selectedDate = selected,
            today = state.today
        )
        val dateInfo = monthInfo.getDate(selected)
        return state.copy(
            currentMonthInfo = monthInfo,
            selectedDateInfo = dateInfo
        )
    }

    fun selectDate(date: LocalDate) {
        _uiState.update {
            val newState = it.copy(selectedDate = date, scrollToDate = null)
            updateCalculatedData(newState)
        }
    }

    fun changeYear(year: Int) {
        val targetDate = _uiState.value.selectedDate.withYear(year)
        _uiState.update { it.copy(viewMode = ViewMode.CALENDAR, scrollToDate = targetDate) }
    }

    fun changeMonth(month: Int) {
        val targetDate = _uiState.value.selectedDate.withMonth(month)
        _uiState.update { it.copy(viewMode = ViewMode.CALENDAR, scrollToDate = targetDate) }
    }

    fun goToToday() {
        val today = LocalDate.now()
        _uiState.update {
            val newState = it.copy(
                today = today,
                selectedDate = today,
                scrollToDate = today,
                viewMode = ViewMode.CALENDAR
            )
            updateCalculatedData(newState)
        }
        refreshWidget()
    }
    
    fun scrollToSelectedDate() {
        _uiState.update { it.copy(scrollToDate = it.selectedDate) }
    }

    fun setViewMode(mode: ViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
    }
    
    fun onScrollHandled() {
        _uiState.update { it.copy(scrollToDate = null) }
    }

    fun updateOption(newOption: Option) {
        viewModelScope.launch {
            optionUseCase.updateOption(newOption)
        }
    }

    fun refreshWidget() {
        viewModelScope.launch {
            WidgetManager.triggerUpdate(getApplication())
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HolidayCalendarApp)
                CalendarViewModel(
                    application.container.holidayUseCase,
                    application.container.optionUseCase,
                    application
                )
            }
        }
    }
}
