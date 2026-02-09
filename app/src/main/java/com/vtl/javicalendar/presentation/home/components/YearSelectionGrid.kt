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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vtl.javicalendar.domain.model.JapaneseHolidays
import com.vtl.javicalendar.presentation.theme.HolidayOrange
import com.vtl.javicalendar.utils.LunarCalendarUtils
import java.time.LocalDate
import java.time.chrono.JapaneseDate
import java.time.format.TextStyle
import java.util.*
import com.vtl.javicalendar.R
@Composable
fun YearSelectionGrid(
    selectedYear: Int,
    holidays: JapaneseHolidays,
    onYearSelected: (Int) -> Unit,
    onCurrentYearClick: () -> Unit
) {
    // Range of 200 years to match the main calendar view: 100 years past and 100 years future
    val currentYear = LocalDate.now().year
    val years = remember { (currentYear - 100..currentYear + 100).toList() }
    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        val initialIndex = years.indexOf(selectedYear)
        if (initialIndex != -1) {
            // Scroll to the selected year, centered roughly
            gridState.scrollToItem(maxOf(0, initialIndex - 6))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onCurrentYearClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(stringResource(R.string.current_year))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = gridState,
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(years, key = { it }) { year ->
                YearItem(
                    year = year,
                    isSelected = year == selectedYear,
                    hasHolidayData = holidays.hasData(year),
                    onClick = { onYearSelected(year) }
                )
            }
        }
    }
}

@Composable
private fun YearItem(
    year: Int,
    isSelected: Boolean,
    hasHolidayData: Boolean,
    onClick: () -> Unit
) {
    // Optimize: only calculate if this year is actually visible
    val itemInfo = remember(year) {
        val date = LocalDate.of(year, 6, 1)
        val jpYear = try {
            val jpDate = JapaneseDate.from(date)
            val era = jpDate.era.getDisplayName(TextStyle.FULL, Locale.JAPAN)
            val eraYear = jpDate.get(java.time.temporal.ChronoField.YEAR_OF_ERA)
            "$era $eraYear"
        } catch (_: Exception) {
            ""
        }
        val lunarDate = LunarCalendarUtils.convertSolarToLunar(1, 6, year)
        Pair(jpYear, lunarDate.yearCanChi)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (itemInfo.first.isNotEmpty()) {
                Text(
                    text = itemInfo.first,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = if (!hasHolidayData) HolidayOrange else Color.Unspecified
                )
            }
            Text(
                text = itemInfo.second,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
