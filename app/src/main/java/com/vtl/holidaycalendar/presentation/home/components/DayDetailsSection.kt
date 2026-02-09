package com.vtl.holidaycalendar.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import com.vtl.holidaycalendar.presentation.model.DateInfo

@Composable
fun DayDetailsSection(
    dateInfo: DateInfo?,
    onYearClick: () -> Unit = {},
    onMonthClick: () -> Unit = {}
) {
    if (dateInfo == null) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Line 1: Year (Era) [space] Day number (Weekday) [space] Month
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Year (Clickable)
            Column(
                modifier = Modifier.weight(1f)
                    .clickable { onYearClick() }
            ) {
                Text(
                    text = dateInfo.year.value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = dateInfo.year.color
                )
                dateInfo.japaneseDate.year?.let {
                    Text(
                        text = it.value,
                        style = MaterialTheme.typography.labelSmall,
                        color = it.color
                    )
                }
                dateInfo.lunarDate.year?.let {
                    Text(
                        text = it.value,
                        style = MaterialTheme.typography.labelSmall,
                        color = it.color
                    )
                }
            }

            // Day number (Weekday)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dateInfo.day.value,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = dateInfo.day.color,
                    lineHeight = 42.sp
                )
                Text(
                    text = dateInfo.weekday.value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = dateInfo.weekday.color
                )
            }

            // Month (Clickable)
            Column(
                modifier = Modifier.weight(1f)
                    .clickable { onMonthClick() },
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = dateInfo.month.value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    color = dateInfo.month.color
                )
                dateInfo.lunarDate.month?.let {
                    Text(
                        text = it.value,
                        style = MaterialTheme.typography.labelSmall,
                        color = it.color
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Line 2: japanese holiday if has
        dateInfo.japaneseDate.holiday?.let {
            Text(
                text = it.value,
                style = MaterialTheme.typography.bodyLarge,
                color = it.color,
                fontWeight = FontWeight.Bold
            )
        }

        // Line 3: lunar full date
        dateInfo.lunarDate.fullDisplayDay?.let {
            Text(
                text = it.value,
                style = MaterialTheme.typography.bodyLarge,
                color = it.color
            )
        }

        // Line 4: observance if has
        dateInfo.lunarDate.observance?.let {
            Text(
                text = it.value,
                style = MaterialTheme.typography.bodyLarge,
                color = it.color,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Line 5: lucDieu full display
        dateInfo.lunarDate.lucDieuFullDisplay?.let {
            Text(
                text = it.value,
                style = MaterialTheme.typography.bodyMedium,
                color = it.color
            )
        }

        // Line 6: auspiciousHours
        dateInfo.lunarDate.auspiciousHours?.let {
            Text(
                text = it.value,
                style = MaterialTheme.typography.bodySmall,
                color = it.color,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
