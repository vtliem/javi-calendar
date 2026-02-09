package com.vtl.holidaycalendar.presentation.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.glance.appwidget.updateAll
import com.vtl.holidaycalendar.HolidayCalendarApp
import com.vtl.holidaycalendar.data.datasource.OptionDataSource
import com.vtl.holidaycalendar.domain.CalendarFactory
import com.vtl.holidaycalendar.presentation.model.DateInfo
import com.vtl.holidaycalendar.presentation.model.MonthInfo
import com.vtl.holidaycalendar.domain.model.JapaneseHolidays
import com.vtl.holidaycalendar.domain.repository.HolidayRepository
import com.vtl.holidaycalendar.presentation.model.Option
import com.vtl.holidaycalendar.widgets.CombinedWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
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
    val option: Option = Option()
)

class CalendarViewModel(
    private val holidayRepository: HolidayRepository,
    private val optionDataSource: OptionDataSource,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHolidays()
        loadOptions()
    }

    private fun loadHolidays() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                holidayRepository.refreshHolidaysIfNeeded()
                val holidays = holidayRepository.getHolidays()
                val newState = _uiState.value.copy(holidays = holidays, isLoading = false, error = null)
                _uiState.value = updateCalculatedData(newState)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun loadOptions() {
        _uiState.value = _uiState.value.copy(option = optionDataSource.loadOption())
    }

    private fun updateCalculatedData(state: HomeUiState): HomeUiState {
        val selected = state.selectedDate
        val monthInfo = CalendarFactory.createMonthInfo(
            selected.year,
            selected.monthValue,
            state.holidays,
            selectedDate = selected
        )
        val dateInfo = monthInfo.getDate(selected)
        return state.copy(
            currentMonthInfo = monthInfo,
            selectedDateInfo = dateInfo
        )
    }

    fun selectDate(date: LocalDate) {
        val newState = _uiState.value.copy(selectedDate = date, scrollToDate = null)
        _uiState.value = updateCalculatedData(newState)
    }

    fun changeYear(year: Int) {
        // We want to scroll to this year/month, but keep selectedDate as is
        val targetDate = _uiState.value.selectedDate.withYear(year)
        _uiState.value = _uiState.value.copy(
            viewMode = ViewMode.CALENDAR,
            scrollToDate = targetDate
        )
    }

    fun changeMonth(month: Int) {
        // We want to scroll to this year/month, but keep selectedDate as is
        val targetDate = _uiState.value.selectedDate.withMonth(month)
        _uiState.value = _uiState.value.copy(
            viewMode = ViewMode.CALENDAR,
            scrollToDate = targetDate
        )
    }

    fun goToToday() {
        val today = LocalDate.now()
        val newState = _uiState.value.copy(
            selectedDate = today, 
            scrollToDate = today,
            viewMode = ViewMode.CALENDAR
        )
        _uiState.value = updateCalculatedData(newState)
    }
    
    fun scrollToSelectedDate() {
        _uiState.value = _uiState.value.copy(scrollToDate = _uiState.value.selectedDate)
    }

    fun setViewMode(mode: ViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = mode)
    }
    
    fun onScrollHandled() {
        _uiState.value = _uiState.value.copy(scrollToDate = null)
    }

    fun updateOption(newOption: Option) {
         _uiState.update {
            if(it == newOption) it
            else it.copy(option = newOption).also {
                Log.v("CalendarViewModel", "updateOption: $newOption")
                optionDataSource.saveOption(newOption)
                viewModelScope.launch {
                    CombinedWidget().updateAll(context)
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HolidayCalendarApp)
                val holidayRepository = application.container.holidayRepository
                val optionDataSource = application.container.optionDataSource
                CalendarViewModel(holidayRepository, optionDataSource, application)
            }
        }
    }
}
