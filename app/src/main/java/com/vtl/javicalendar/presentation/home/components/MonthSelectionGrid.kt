package com.vtl.javicalendar.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthSelectionGrid(
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit,
) {
  val locale = Locale.getDefault()
  val months = Month.entries.toTypedArray()

  Column(modifier = Modifier.fillMaxSize()) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
      itemsIndexed(months) { index, month ->
        val monthValue = index + 1
        MonthItem(
            monthName = month.getDisplayName(TextStyle.FULL, locale),
            isSelected = monthValue == selectedMonth,
            onClick = { onMonthSelected(monthValue) },
        )
      }
    }
  }
}

@Composable
private fun MonthItem(monthName: String, isSelected: Boolean, onClick: () -> Unit) {
  Card(
      modifier = Modifier.fillMaxWidth().clickable { onClick() },
      colors =
          CardDefaults.cardColors(
              containerColor =
                  if (isSelected) MaterialTheme.colorScheme.primaryContainer
                  else MaterialTheme.colorScheme.surfaceVariant
          ),
  ) {
    Box(modifier = Modifier.padding(16.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
      Text(
          text = monthName,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
      )
    }
  }
}
