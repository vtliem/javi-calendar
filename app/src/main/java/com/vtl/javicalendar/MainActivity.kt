package com.vtl.javicalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vtl.javicalendar.presentation.home.CalendarViewModel
import com.vtl.javicalendar.presentation.home.ViewMode
import com.vtl.javicalendar.presentation.home.components.*
import com.vtl.javicalendar.presentation.theme.HolidayCalendarTheme
import com.vtl.javicalendar.domain.CalendarFactory
import com.vtl.javicalendar.presentation.theme.HolidayOrange
import com.vtl.javicalendar.utils.LunarCalendarUtils
import com.vtl.javicalendar.widgets.WidgetManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.chrono.JapaneseDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HolidayCalendarTheme {
                val vm: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
                val uiState by vm.uiState.collectAsState()
                val isTodaySelected = remember(uiState.selectedDate) {
                    uiState.selectedDate == LocalDate.now()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        Column(horizontalAlignment = Alignment.End) {
                            if (uiState.viewMode == ViewMode.CALENDAR) {
                                FloatingActionButton(
                                    onClick = { vm.setViewMode(ViewMode.SETTINGS) },
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                                }
                            }
                            
                            if (uiState.viewMode == ViewMode.CALENDAR && !isTodaySelected) {
                                FloatingActionButton(
                                    onClick = { vm.goToToday() },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Icon(Icons.Default.Today, contentDescription = "Go to Today")
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        CalendarView(
                            uiState = uiState,
                            onDateSelected = { vm.selectDate(it) },
                            onYearClick = { vm.setViewMode(ViewMode.YEAR_SELECT) },
                            onMonthClick = { vm.setViewMode(ViewMode.MONTH_SELECT) },
                            onDayClick = { vm.scrollToSelectedDate() },
                            onScrollHandled = { vm.onScrollHandled() }
                        )

                        if (uiState.viewMode != ViewMode.CALENDAR) {
                            ModalBottomSheet(
                                onDismissRequest = { vm.setViewMode(ViewMode.CALENDAR) },
                                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                            ) {
                                Box(modifier = Modifier.fillMaxHeight(0.8f)) {
                                    when (uiState.viewMode) {
                                        ViewMode.YEAR_SELECT -> {
                                            YearSelectionGrid(
                                                selectedYear = uiState.selectedDate.year,
                                                holidays = uiState.holidays,
                                                onYearSelected = { vm.changeYear(it) },
                                                onCurrentYearClick = { vm.goToToday() }
                                            )
                                        }
                                        ViewMode.MONTH_SELECT -> {
                                            MonthSelectionGrid(
                                                selectedMonth = uiState.selectedDate.monthValue,
                                                onMonthSelected = { vm.changeMonth(it) },
                                                onTodayClick = { vm.goToToday() }
                                            )
                                        }
                                        ViewMode.SETTINGS -> {
                                            SettingsSection(
                                                option = uiState.option,
                                                onOptionChanged = { vm.updateOption(it) }
                                            )
                                        }
                                        else -> {}
                                    }
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
            val calendarSourcesUseCase = (applicationContext as HolidayCalendarApp).container.calendarSourcesUseCase
            if(!calendarSourcesUseCase.refresh()){
                val sources = calendarSourcesUseCase().first()
                WidgetManager.triggerUpdate(applicationContext, sources)
            }
        }
    }
}

@Composable
fun CalendarView(
    uiState: com.vtl.javicalendar.presentation.home.HomeUiState,
    onDateSelected: (LocalDate) -> Unit,
    onYearClick: () -> Unit,
    onMonthClick: () -> Unit,
    onDayClick: () -> Unit,
    onScrollHandled: () -> Unit
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
            val monthsDiff = ChronoUnit.MONTHS.between(
                referenceMonth,
                date.withDayOfMonth(1)
            ).toInt()
            val targetIndex = monthsDiff.coerceIn(0, totalItems - 1)
            if (listState.firstVisibleItemIndex != targetIndex || listState.firstVisibleItemScrollOffset != 0) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        DayDetailsSection(
            dateInfo = uiState.selectedDateInfo,
            option = uiState.option,
            onYearClick = onYearClick,
            onMonthClick = onMonthClick,
            onDayClick = onDayClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 200.dp)
        ) {
            items(totalItems) { index ->
                val monthDate = referenceMonth.plusMonths(index.toLong())
                val monthInfo = remember(monthDate.year, monthDate.monthValue, uiState.holidays, 
                    uiState.selectedDate.let { if (it.year == monthDate.year && it.monthValue == monthDate.monthValue) it else null }) {
                    CalendarFactory.createMonthInfo(
                        monthDate.year,
                        monthDate.monthValue,
                        uiState.holidays,
                        selectedDate = uiState.selectedDate,
                        today = LocalDate.now()
                    )
                }

                val headerInfo = remember(monthDate, uiState.holidays, uiState.option.japaneseInfo) {
                    val jpYear = if (uiState.option.japaneseInfo) {
                        try {
                            val jpDate = JapaneseDate.from(monthDate)
                            val era = jpDate.era.getDisplayName(TextStyle.FULL, Locale.JAPAN)
                            val eraYear = jpDate.get(java.time.temporal.ChronoField.YEAR_OF_ERA)
                            "$era $eraYear"
                        } catch (_: Exception) { "" }
                    } else ""
                    val lunarDate = LunarCalendarUtils.convertSolarToLunar(1, monthDate.monthValue, monthDate.year)
                    val hasHolidayData = uiState.holidays.hasData(monthDate.year)
                    Triple(jpYear, lunarDate.yearCanChi, hasHolidayData)
                }

                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = monthDate.month.getDisplayName(TextStyle.FULL, locale) + " " + monthDate.year,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            if (headerInfo.first.isNotEmpty()) {
                                Text(
                                    text = "(${headerInfo.first})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (!headerInfo.third) HolidayOrange else MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = "(${headerInfo.second})",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    MonthGridSection(
                        monthInfo = monthInfo,
                        option = uiState.option,
                        onDateSelected = onDateSelected
                    )
                }
            }
        }
    }
}
