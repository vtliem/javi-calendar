package com.vtl.holidaycalendar.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vtl.holidaycalendar.HolidayCalendarApp
import com.vtl.holidaycalendar.domain.CalendarFactory
import com.vtl.holidaycalendar.presentation.model.DateInfo
import com.vtl.holidaycalendar.presentation.model.MonthInfo
import com.vtl.holidaycalendar.domain.model.JapaneseHolidays
import com.vtl.holidaycalendar.domain.repository.HolidayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val holidays: JapaneseHolidays = JapaneseHolidays(emptyMap()),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonthInfo: MonthInfo? = null,
    val selectedDateInfo: DateInfo? = null
)

class CalendarViewModel(
    private val holidayRepository: HolidayRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHolidays()
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

    private fun updateCalculatedData(state: HomeUiState): HomeUiState {
        val selected = state.selectedDate
        val monthInfo = CalendarFactory.createMonthInfo(
            selected.year,
            selected.monthValue,
            state.holidays
        )
        val dateInfo = monthInfo.getDate(selected)
        return state.copy(
            currentMonthInfo = monthInfo,
            selectedDateInfo = dateInfo
        )
    }

    fun selectDate(date: LocalDate) {
        val newState = _uiState.value.copy(selectedDate = date)
        _uiState.value = updateCalculatedData(newState)
    }

    fun nextMonth() {
        val next = _uiState.value.selectedDate.plusMonths(1).withDayOfMonth(1)
        selectDate(next)
    }

    fun previousMonth() {
        val prev = _uiState.value.selectedDate.minusMonths(1).withDayOfMonth(1)
        selectDate(prev)
    }

    fun changeYear(year: Int) {
        val newDate = _uiState.value.selectedDate.withYear(year)
        selectDate(newDate)
    }

    fun changeMonth(month: Int) {
        val newDate = _uiState.value.selectedDate.withMonth(month)
        selectDate(newDate)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HolidayCalendarApp)
                val holidayRepository = application.container.holidayRepository
                CalendarViewModel(holidayRepository)
            }
        }
    }
}
