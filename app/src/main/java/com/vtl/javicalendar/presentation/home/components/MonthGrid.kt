package com.vtl.javicalendar.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vtl.javicalendar.domain.model.LunarDate
import com.vtl.javicalendar.presentation.model.DateInfo
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.color
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.shortName
import com.vtl.javicalendar.presentation.model.MonthInfo
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.presentation.model.ZodiacDisplay
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MonthGridSection(
    monthInfo: MonthInfo?,
    option: Option = Option(),
    onDateSelected: (LocalDate) -> Unit,
) {
  if (monthInfo == null) return

  val allLunarDates by
      produceState<Map<LocalDate, LunarDate>?>(initialValue = null, monthInfo, option.month) {
        value =
            if (option.month.lunarDate) {
              withContext(Dispatchers.Default) {
                monthInfo.weeks.flatten().filterNotNull().associate { it.value to it.lunarDate }
              }
            } else {
              null
            }
      }

  Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
    // Weekday Headers
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
      monthInfo.daysOfWeek.forEach { dayOfWeek ->
        Text(
            text = dayOfWeek.shortName,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium,
            color = dayOfWeek.color ?: MaterialTheme.colorScheme.onSurface,
        )
      }
    }

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 2.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )

    monthInfo.weeks.forEach { week ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        week.forEach { dateInfo ->
          if (dateInfo == null) {
            Spacer(modifier = Modifier.weight(1f))
          } else {
            DayCell(
                dateInfo = dateInfo,
                lunarDate = allLunarDates?.get(dateInfo.value),
                option = option,
                modifier = Modifier.weight(1f).clickable { onDateSelected(dateInfo.value) },
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
    lunarDate: LunarDate?,
    modifier: Modifier = Modifier,
    option: Option = Option(),
) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier =
          modifier.defaultMinSize(minHeight = 42.dp).let {
            dateInfo.border?.let { color -> it.border(1.dp, color, RoundedCornerShape(4.dp)) } ?: it
          },
  ) {
    // Line 1: Solar Day + Lunar Day
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(6.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      if (option.month.lunarDate && lunarDate != null) {
        val lunarDayText =
            if (lunarDate.day.value == 1) "${lunarDate.day.value}/${lunarDate.month.value}"
            else lunarDate.day.value.toString()
        Text(
            text = lunarDayText,
            style = MaterialTheme.typography.labelSmall,
            color =
                dateInfo.colorOfLunarDay(option.month.zodiac)
                    ?: MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Text(
          text = dateInfo.value.dayOfMonth.toString(),
          style = MaterialTheme.typography.bodyMedium,
          color = dateInfo.colorOfDay ?: MaterialTheme.colorScheme.onSurface,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
          modifier =
              dateInfo.backgroundColorOfDay?.let {
                Modifier.size(
                        with(LocalDensity.current) {
                          MaterialTheme.typography.bodyMedium.lineHeight.toDp()
                        }
                    )
                    .background(it, shape = CircleShape)
              } ?: Modifier,
      )
    }
    // Line 2: Holiday
    if (option.month.japaneseDate) {
      dateInfo.japaneseHoliday?.let {
        Text(
            text = it,
            fontSize = 7.sp,
            color = dateInfo.colorOfJapaneseHoliday,
            textAlign = TextAlign.Center,
            lineHeight = 8.sp,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
        )
      }
    }

    // Line 3: Zodiac
    if (option.month.zodiac == ZodiacDisplay.Full && lunarDate != null) {
      Text(
          text = lunarDate.zodiac.zodiacName,
          fontSize = 8.sp,
          color = lunarDate.zodiac.color ?: MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
      )
    }

    if (option.month.observance && lunarDate != null) {
      lunarDate.observance?.let {
        Text(
            text = it,
            fontSize = 7.sp,
            color = dateInfo.colorOfObservance,
            textAlign = TextAlign.Center,
            lineHeight = 8.sp,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
        )
      }
    }
  }
}
