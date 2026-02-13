package com.vtl.javicalendar.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vtl.javicalendar.domain.model.JapaneseHolidays
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.japaneseYear
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.lunarDate
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.presentation.theme.Auspicious
import java.time.LocalDate

@Composable
fun YearSelectionGrid(
    selectedYear: Int,
    holidays: JapaneseHolidays,
    option: Option,
    onYearSelected: (Int) -> Unit,
) {
  // Range of 200 years to match the main calendar view: 100 years past and 100 years future
  val currentYear = LocalDate.now().year
  val years = remember { (currentYear - 100..currentYear + 100).toList() }
  val initialIndex = remember {
    val index = years.indexOf(selectedYear)
    if (index != -1) maxOf(0, index - 6) else 0
  }

  val gridState = rememberLazyGridState(
    initialFirstVisibleItemIndex = initialIndex
  )

  Column(modifier = Modifier.fillMaxSize()) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
      items(years, key = { it }) { year ->
        YearItem(
            option = option,
            year = year,
            isSelected = year == selectedYear,
            hasHolidayData = holidays.hasData(year),
            onClick = { onYearSelected(year) },
        )
      }
    }
  }
}

@Composable
private fun YearItem(option: Option, year: Int, isSelected: Boolean, hasHolidayData: Boolean, onClick: () -> Unit) {
  // 1. Use produceState to move calculation to a background thread
  // This returns a State object that starts with a "Loading" or Empty value
  val itemInfo by produceState<Pair<String, String>?>(initialValue = null, year) {
    // This runs in a Dispatcher.Default (background) coroutine
    val date = LocalDate.of(year, 6, 1)
    value = Pair(
      if(option.dayDetail.japaneseDate) date.japaneseYear else "",
      if(option.dayDetail.lunarDate) date.lunarDate.year.shortName else ""
    )
  }

  Card(
    modifier = Modifier.fillMaxWidth().clickable { onClick() },
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
      else MaterialTheme.colorScheme.surfaceVariant
    ),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(8.dp).heightIn(min = 80.dp), // Fixed height prevents jumping
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = year.toString(),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
      )

      // Only show if the background calculation is finished
      itemInfo?.let {
        val (japaneseYear, lunarYear) = it
        if (japaneseYear.isNotEmpty()) {
          Text(
            text = japaneseYear,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = if (!hasHolidayData) Auspicious else Color.Unspecified,
          )
        }
        Text(
          text = lunarYear,
          style = MaterialTheme.typography.labelSmall,
          textAlign = TextAlign.Center,
        )
      }
    }
  }
}
