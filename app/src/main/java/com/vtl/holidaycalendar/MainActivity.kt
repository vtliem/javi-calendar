package com.vtl.holidaycalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vtl.holidaycalendar.presentation.home.CalendarViewModel
import com.vtl.holidaycalendar.presentation.home.components.*
import com.vtl.holidaycalendar.presentation.theme.HolidayCalendarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HolidayCalendarTheme {
                val viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
                val uiState by viewModel.uiState.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 8.dp)
                    ) {
                        DayDetailsSection(
                            dateInfo = uiState.selectedDateInfo,
                            onYearClick = { 
                                viewModel.changeYear(uiState.selectedDate.year + 1)
                            },
                            onMonthClick = {
                                val nextMonth = if (uiState.selectedDate.monthValue == 12) 1 else uiState.selectedDate.monthValue + 1
                                viewModel.changeMonth(nextMonth)
                            }
                        )
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        
                        MonthGridSection(
                            monthInfo = uiState.currentMonthInfo,
                            onDateSelected = { date -> viewModel.selectDate(date) }
                        )
                    }
                }
            }
        }
    }
}
