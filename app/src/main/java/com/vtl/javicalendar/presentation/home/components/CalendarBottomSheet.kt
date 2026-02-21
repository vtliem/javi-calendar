package com.vtl.javicalendar.presentation.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vtl.javicalendar.presentation.home.HomeUiState
import com.vtl.javicalendar.presentation.home.ViewMode

@Composable
fun CalendarBottomSheet(
    uiState: HomeUiState,
    onDismissRequest: () -> Unit,
    onYearSelected: (Int) -> Unit,
    onMonthSelected: (Int) -> Unit,
) {
  ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
  ) {
    LimitFontScale(uiState.option.maxFontScale) {
      Box(modifier = Modifier.fillMaxHeight(0.8f)) {
        when (uiState.viewMode) {
          ViewMode.YEAR_SELECT -> {
            YearSelectionGrid(
                selectedYear = uiState.selectedDate.year,
                holidays = uiState.holidays,
                option = uiState.option,
                onYearSelected = onYearSelected,
            )
          }

          ViewMode.MONTH_SELECT -> {
            MonthSelectionGrid(
                selectedMonth = uiState.selectedDate.monthValue,
                onMonthSelected = onMonthSelected,
            )
          }

          else -> {}
        }
      }
    }
  }
}
