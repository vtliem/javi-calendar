package com.vtl.javicalendar.presentation.home.components

import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vtl.javicalendar.domain.CalendarFactory
import com.vtl.javicalendar.presentation.home.HomeUiState
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.japaneseYear
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.lunarDate
import com.vtl.javicalendar.presentation.theme.Auspicious
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

@Composable
fun CalendarView(
    uiState: HomeUiState,
    onDateSelected: (LocalDate) -> Unit,
    onYearClick: () -> Unit,
    onMonthClick: () -> Unit,
    onDayClick: () -> Unit,
    onScrollHandled: () -> Unit,
) {
  val totalItems = 2400
  val referenceMonth = remember { LocalDate.now().withDayOfMonth(1).minusYears(100) }
  val nowIndex = remember {
    ChronoUnit.MONTHS.between(referenceMonth, LocalDate.now().withDayOfMonth(1)).toInt()
  }
  val listState = rememberLazyListState(initialFirstVisibleItemIndex = nowIndex)
  val snapLayoutInfoProvider = remember(listState) { SnapLayoutInfoProvider(listState) }
  val snapFlingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider)
  val locale = Locale.getDefault()

  LaunchedEffect(uiState.scrollToDate) {
    uiState.scrollToDate?.let { date ->
      val monthsDiff = ChronoUnit.MONTHS.between(referenceMonth, date.withDayOfMonth(1)).toInt()
      val targetIndex = monthsDiff.coerceIn(0, totalItems - 1)
      if (
          listState.firstVisibleItemIndex != targetIndex ||
              listState.firstVisibleItemScrollOffset != 0
      ) {
        val currentIdx = listState.firstVisibleItemIndex
        if (abs(currentIdx - targetIndex) > 12) {
          val intermediateIdx = if (targetIndex > currentIdx) targetIndex - 1 else targetIndex + 1
          listState.scrollToItem(intermediateIdx)
        }
        listState.animateScrollToItem(targetIndex)
      }
      onScrollHandled()
    }
  }

  Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
    DayDetailsSection(
        dateInfo = uiState.selectedDateInfo,
        option = uiState.option,
        onYearClick = onYearClick,
        onMonthClick = onMonthClick,
        onDayClick = onDayClick,
    )

    LazyColumn(
        state = listState,
        flingBehavior = snapFlingBehavior,
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(bottom = 200.dp),
    ) {
      items(totalItems) { index ->
        val monthDate = referenceMonth.plusMonths(index.toLong())
        val monthInfo =
            remember(
                monthDate.year,
                monthDate.monthValue,
                uiState.holidays,
                uiState.selectedDate.let {
                  if (it.year == monthDate.year && it.monthValue == monthDate.monthValue) it
                  else null
                },
            ) {
              CalendarFactory.createMonthInfo(
                  monthDate.year,
                  monthDate.monthValue,
                  uiState.holidays,
                  selectedDate = uiState.selectedDate,
                  today = LocalDate.now(),
              )
            }

        val headerInfo =
            remember(monthDate, uiState.holidays, uiState.option.month.japaneseDate) {
              val jpYear =
                  if (uiState.option.month.japaneseDate) {
                    monthDate.japaneseYear
                  } else ""
              val lunarYear =
                  if (uiState.option.month.lunarDate) {
                    monthDate.lunarDate.let {
                      if (it.year.value != monthDate.year) it.year.displayName
                      else it.year.shortName
                    }
                  } else ""
              val hasHolidayData = uiState.holidays.hasData(monthDate.year)
              Triple(jpYear, lunarYear, hasHolidayData)
            }

        Column(modifier = Modifier.padding(bottom = 24.dp)) {
          Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Bottom,
          ) {
            Text(
                text =
                    monthDate.month.getDisplayName(TextStyle.FULL, locale) + " " + monthDate.year,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(verticalAlignment = Alignment.Bottom) {
              if (headerInfo.first.isNotEmpty()) {
                Text(
                    text = "(${headerInfo.first})",
                    style = MaterialTheme.typography.labelMedium,
                    color =
                        if (!headerInfo.third) Auspicious else MaterialTheme.colorScheme.secondary,
                )
                Spacer(modifier = Modifier.width(4.dp))
              }
              if (headerInfo.second.isNotEmpty()) {
                Text(
                    text = "(${headerInfo.second})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
              }
            }
          }
          MonthGridSection(
              monthInfo = monthInfo,
              option = uiState.option,
              onDateSelected = onDateSelected,
          )
        }
      }
    }
  }
}
