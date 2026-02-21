package com.vtl.javicalendar.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
      modifier = Modifier.fillMaxWidth().padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
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

    if (option.dayDetail.japaneseDate) {
      dateInfo.japaneseLongHoliday?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodyLarge,
            color = dateInfo.colorOfJapaneseHoliday,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
      }
    }

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
    if (option.dayDetail.lunarDate && option.dayDetail.zodiac == ZodiacDisplay.Full) {
      ZodiacDetail(dateInfo)
      Spacer(modifier = Modifier.height(4.dp))
    }
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

@Composable
private fun ZodiacDetail(dateInfo: DateInfo) {
  var expanded by remember { mutableStateOf(true) }
  val zodiac = dateInfo.lunarDate.zodiac
  val duty = dateInfo.lunarDate.duty
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
        modifier =
            Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          text = zodiac.toString(),
          style = MaterialTheme.typography.bodyMedium,
          color = zodiac.color ?: MaterialTheme.colorScheme.onSurface,
      )
      Icon(
          imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = if (expanded) "Collapse" else "Expand",
          modifier = Modifier.size(20.dp),
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    if (expanded) {
      Text(
          text = zodiac.detail,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
              text = "Trực:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
              text = duty.dutyName,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface,
          )
          dateInfo.lunarDate.solarTermName?.let {
            Text(
                text = "Tiết khí:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp),
            )
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
        if (duty.goodFor.isNotEmpty()) {
          Text(
              text = duty.goodFor,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(start = 16.dp),
          )
        }
        if (duty.badFor.isNotEmpty()) {
          Text(
              text = duty.badFor,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(start = 16.dp),
          )
        }
      }
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Giờ Hoàng Đạo:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = dateInfo.lunarDate.auspiciousHours,
            style = MaterialTheme.typography.bodySmall,
            color = dateInfo.colorOfAuspiciousHours,
        )
      }
    }
  }
}
