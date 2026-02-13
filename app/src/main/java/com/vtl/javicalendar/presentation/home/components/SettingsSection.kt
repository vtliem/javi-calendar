package com.vtl.javicalendar.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vtl.javicalendar.R
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.presentation.model.ZodiacDisplay

@Composable
fun SettingsSection(option: Option, onOptionChanged: (Option) -> Unit) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
    Text(
        text = stringResource(R.string.settings_title),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 24.dp),
    )

    // Sunday First at top
    SettingSwitchItem(
        label = stringResource(R.string.settings_sunday_first),
        checked = option.sundayFirst,
        onCheckedChange = { onOptionChanged(option.copy(sundayFirst = it)) },
    )

    Spacer(modifier = Modifier.height(24.dp))

    // 3-Column Header
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
      Spacer(modifier = Modifier.weight(1.5f))
      Text(
          text = stringResource(R.string.settings_section_day_detail),
          style = MaterialTheme.typography.titleSmall,
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
      )
      Text(
          text = stringResource(R.string.settings_section_month),
          style = MaterialTheme.typography.titleSmall,
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
      )
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

    // Japanese Date Row
    SettingsRow(
        label = stringResource(R.string.settings_japanese_date),
        dayValue = option.dayDetail.japaneseDate,
        monthValue = option.month.japaneseDate,
        onDayChanged = {
          onOptionChanged(option.copy(dayDetail = option.dayDetail.copy(japaneseDate = it)))
        },
        onMonthChanged = {
          onOptionChanged(option.copy(month = option.month.copy(japaneseDate = it)))
        },
    )

    // Lunar Date Row
    SettingsRow(
        label = stringResource(R.string.settings_lunar_date),
        dayValue = option.dayDetail.lunarDate,
        monthValue = option.month.lunarDate,
        onDayChanged = {
          onOptionChanged(option.copy(dayDetail = option.dayDetail.copy(lunarDate = it)))
        },
        onMonthChanged = {
          onOptionChanged(option.copy(month = option.month.copy(lunarDate = it)))
        },
    )

    // Observance Row
    SettingsRow(
        label = stringResource(R.string.settings_observance),
        dayValue = option.dayDetail.observance,
        monthValue = option.month.observance,
        onDayChanged = {
          onOptionChanged(option.copy(dayDetail = option.dayDetail.copy(observance = it)))
        },
        onMonthChanged = {
          onOptionChanged(option.copy(month = option.month.copy(observance = it)))
        },
        dayEnabled = option.dayDetail.lunarDate,
        monthEnabled = option.month.lunarDate,
    )

    // Zodiac Row
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
      Text(
          text = stringResource(R.string.settings_zodiac),
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.weight(1.5f),
          color =
              if (option.dayDetail.lunarDate || option.month.lunarDate)
                  MaterialTheme.colorScheme.onSurface
              else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
      )
      Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
        ZodiacSelector(
            selected = option.dayDetail.zodiac,
            onSelected = {
              onOptionChanged(option.copy(dayDetail = option.dayDetail.copy(zodiac = it)))
            },
            enabled = option.dayDetail.lunarDate,
        )
      }
      Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
        ZodiacSelector(
            selected = option.month.zodiac,
            onSelected = { onOptionChanged(option.copy(month = option.month.copy(zodiac = it))) },
            enabled = option.month.lunarDate,
        )
      }
    }
  }
}

@Composable
private fun SettingsRow(
    label: String,
    dayValue: Boolean,
    monthValue: Boolean,
    onDayChanged: (Boolean) -> Unit,
    onMonthChanged: (Boolean) -> Unit,
    dayEnabled: Boolean = true,
    monthEnabled: Boolean = true,
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
  ) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.weight(1.5f),
        color =
            if (dayEnabled || monthEnabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    )
    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
      Switch(checked = dayValue, onCheckedChange = onDayChanged, enabled = dayEnabled)
    }
    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
      Switch(checked = monthValue, onCheckedChange = onMonthChanged, enabled = monthEnabled)
    }
  }
}

@Composable
private fun ZodiacSelector(
    selected: ZodiacDisplay,
    onSelected: (ZodiacDisplay) -> Unit,
    enabled: Boolean = true,
) {
  var expanded by remember { mutableStateOf(false) }

  Box {
    TextButton(
        onClick = { expanded = true },
        contentPadding = PaddingValues(0.dp),
        enabled = enabled,
    ) {
      Text(text = getZodiacDisplayName(selected), style = MaterialTheme.typography.bodySmall)
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      ZodiacDisplay.entries.forEach { display ->
        DropdownMenuItem(
            text = { Text(getZodiacDisplayName(display)) },
            onClick = {
              onSelected(display)
              expanded = false
            },
        )
      }
    }
  }
}

@Composable
private fun getZodiacDisplayName(display: ZodiacDisplay): String {
  return when (display) {
    ZodiacDisplay.Full -> stringResource(R.string.zodiac_display_full)
    ZodiacDisplay.Short -> stringResource(R.string.zodiac_display_short)
    ZodiacDisplay.None -> stringResource(R.string.zodiac_display_none)
  }
}

@Composable
private fun SettingSwitchItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(
      modifier =
          Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onCheckedChange(!checked) },
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(text = label, style = MaterialTheme.typography.bodyLarge)
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}
