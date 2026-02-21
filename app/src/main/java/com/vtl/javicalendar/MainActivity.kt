package com.vtl.javicalendar

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vtl.javicalendar.presentation.home.CalendarViewModel
import com.vtl.javicalendar.presentation.home.ViewMode
import com.vtl.javicalendar.presentation.home.components.*
import com.vtl.javicalendar.presentation.theme.JaviCalendarTheme
import com.vtl.javicalendar.widgets.WidgetManager
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      JaviCalendarTheme {
        val vm: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
        val uiState by vm.uiState.collectAsState()
        val isTodaySelected =
            remember(uiState.selectedDate) { uiState.selectedDate == LocalDate.now() }
        LimitFontScale(uiState.option.maxFontScale) {
          if (uiState.viewMode == ViewMode.SETTINGS) {
            SettingsSection(
                option = uiState.option,
                onOptionChanged = { vm.updateOption(it) },
                holidays = uiState.holidays,
                onSyncClick = { vm.syncJapaneseHolidays() },
                onBackClick = { vm.setViewMode(ViewMode.CALENDAR) },
            )
          } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                floatingActionButton = {
                  Column(horizontalAlignment = Alignment.End) {
                    if (uiState.viewMode == ViewMode.CALENDAR) {
                      Box(contentAlignment = Alignment.TopEnd) {
                        FloatingActionButton(
                            onClick = { vm.setViewMode(ViewMode.SETTINGS) },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp),
                        ) {
                          Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                        uiState.holidays.error?.let {
                          HolidayErrorIndicator(
                              error = it,
                              modifier = Modifier.padding(top = 4.dp, end = 4.dp),
                          )
                        }
                      }
                    }

                    if (uiState.viewMode == ViewMode.CALENDAR && !isTodaySelected) {
                      FloatingActionButton(
                          onClick = { vm.goToToday() },
                          containerColor = MaterialTheme.colorScheme.primary,
                          contentColor = MaterialTheme.colorScheme.onPrimary,
                      ) {
                        Icon(Icons.Default.Today, contentDescription = "Go to Today")
                      }
                    }
                  }
                },
            ) { innerPadding ->
              Box(modifier = Modifier.padding(innerPadding)) {
                CalendarView(
                    uiState = uiState,
                    onDateSelected = { vm.selectDate(it) },
                    onYearClick = { vm.setViewMode(ViewMode.YEAR_SELECT) },
                    onMonthClick = { vm.setViewMode(ViewMode.MONTH_SELECT) },
                    onDayClick = { vm.scrollToSelectedDate() },
                    onScrollHandled = { vm.onScrollHandled() },
                )

                if (uiState.viewMode != ViewMode.CALENDAR) {
                  CalendarBottomSheet(
                      uiState = uiState,
                      onDismissRequest = { vm.setViewMode(ViewMode.CALENDAR) },
                      onYearSelected = { vm.changeYear(it) },
                      onMonthSelected = { vm.changeMonth(it) },
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    lifecycleScope.launch {
      val calendarSourcesUseCase =
          (applicationContext as JaviCalendarApp).container.calendarSourcesUseCase
      if (!calendarSourcesUseCase.refresh()) {
        val sources = calendarSourcesUseCase().first()
        Log.v("MainActivity", "triggerUpdate onResume")
        WidgetManager.triggerUpdate(applicationContext, sources)
      }
    }
  }
}
