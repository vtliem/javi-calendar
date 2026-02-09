package com.vtl.javicalendar.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vtl.javicalendar.presentation.model.DateInfo
import com.vtl.javicalendar.presentation.model.MonthInfo
import com.vtl.javicalendar.presentation.model.Option
import java.time.LocalDate


@Composable
fun MonthGridSection(
    monthInfo: MonthInfo?, 
    option: Option = Option(),
    onDateSelected: (LocalDate) -> Unit
) {
    if (monthInfo == null) return

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        // Weekday Headers
        Row(modifier = Modifier.fillMaxWidth()) {
            monthInfo.weekDayNames.forEach { dayField ->
                Text(
                    text = dayField.value,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = dayField.color
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 2.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )


        monthInfo.weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                week.forEach { dateInfo ->
                    if (dateInfo == null) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        DayCell(
                            dateInfo = dateInfo,
                            option = option,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onDateSelected(dateInfo.value) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    dateInfo: DateInfo,
    modifier: Modifier = Modifier,
    option: Option = Option(),
) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.defaultMinSize(minHeight = 32.dp)) {
            // Line 1: Solar Day + Lunar Day
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                    dateInfo.lunarDate.day?.let {
                        Text(
                            text = it.value,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                Text(
                    text = dateInfo.day.value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = dateInfo.day.color,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = dateInfo.day.backgroundColor?.let {
                        Modifier.size( with(LocalDensity.current) {
                            MaterialTheme.typography.bodyMedium.lineHeight.toDp()
                        }).background(
                            it,
                            shape = CircleShape
                        )
                    } ?: Modifier
                )
            }

            // Line 2: Lunar Day Name (lucDieu)
            if (option.monthLucDieu) {
                dateInfo.lunarDate.lucDieu?.let {
                    Text(
                        text = it.value,
                        fontSize = 8.sp,
                        color = it.color,
                        maxLines = 1
                    )
                }
            }

            // Line 3 & 4: Observances
            if (option.monthJapaneseHoliday) {
                dateInfo.japaneseDate.holiday?.let {
                    Text(
                        text = it.value,
                        fontSize = 7.sp,
                        color = it.color,
                        textAlign = TextAlign.Center,
                        lineHeight = 8.sp,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (option.monthObservance) {
                dateInfo.lunarDate.observance?.let {
                    Text(
                        text = it.value,
                        fontSize = 7.sp,
                        color = it.color,
                        textAlign = TextAlign.Center,
                        lineHeight = 8.sp,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
}
