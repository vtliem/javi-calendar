package com.vtl.javicalendar.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vtl.javicalendar.presentation.model.DateInfo
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.color
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.displayName
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.monthName
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.presentation.model.ZodiacDisplay

@Composable
fun DayDetailsSection(
    dateInfo: DateInfo?,
    option: Option = Option(),
    onYearClick: () -> Unit = {},
    onMonthClick: () -> Unit = {},
    onDayClick: () -> Unit = {},
) {
  if (dateInfo == null) return

  Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // Line 1: Year (Era) [space] Day number (Weekday) [space] Month
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      // Year (Clickable)
      Column(modifier = Modifier.weight(1f).clickable { onYearClick() }) {
        Text(
            text = dateInfo.value.year.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        if (option.dayDetail.japaneseDate) {
          Text(
              text = dateInfo.japaneseYear,
              style = MaterialTheme.typography.labelSmall,
              color = dateInfo.colorOfJapaneseYear ?: MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        if (option.dayDetail.lunarDate) {
          Text(
              text = dateInfo.lunarYear,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      // Day number (Weekday)
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.clickable { onDayClick() },
      ) {
        Text(
            text = dateInfo.value.dayOfMonth.toString(),
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            color = dateInfo.colorOfDay ?: MaterialTheme.colorScheme.onSurface,
            lineHeight = 42.sp,
        )
        Text(
            text = dateInfo.value.dayOfWeek.displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = dateInfo.value.dayOfWeek.color ?: MaterialTheme.colorScheme.onSurface,
        )
      }

      // Month (Clickable)
      Column(
          modifier = Modifier.weight(1f).clickable { onMonthClick() },
          horizontalAlignment = Alignment.End,
      ) {
        Text(
            text = dateInfo.value.monthName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
        )
        if (option.dayDetail.lunarDate) {
          Text(
              text = dateInfo.lunarDate.month.displayName,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Line 2: japanese holiday if has
    if (option.dayDetail.japaneseDate) {
      dateInfo.japaneseHoliday?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodyLarge,
            color = dateInfo.colorOfJapaneseHoliday,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
      }
    }

    // Line 3: lunar day
    if (option.dayDetail.lunarDate) {
      Text(
          text = dateInfo.lunarDate.day.displayName,
          style = MaterialTheme.typography.bodyLarge,
          color =
              dateInfo.colorOfLunarDay(option.dayDetail.zodiac)
                  ?: MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(4.dp))
    }
    //  zodiac full display
    if (option.dayDetail.lunarDate && option.dayDetail.zodiac === ZodiacDisplay.Full) {
      Text(
          text = dateInfo.lunarDate.zodiac.toString(),
          style = MaterialTheme.typography.bodyMedium,
          color = dateInfo.lunarDate.zodiac.color ?: MaterialTheme.colorScheme.onSurface,
      )
      // line 5: Detail
      Text(
          text = dateInfo.lunarDate.zodiac.detail,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      // Line 7: auspiciousHours
      Text(
          text = dateInfo.lunarDate.auspiciousHours,
          style = MaterialTheme.typography.bodySmall,
          color = dateInfo.colorOfAuspiciousHours,
          textAlign = TextAlign.Center,
          lineHeight = 16.sp,
          modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(4.dp))
    }
    // observance if has
    if (option.dayDetail.lunarDate && option.dayDetail.observance) {
      dateInfo.lunarDate.observance?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodyLarge,
            color = dateInfo.colorOfObservance,
            fontWeight = FontWeight.Bold,
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
    }
  }
}
