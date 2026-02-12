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
import androidx.compose.ui.unit.dp
import com.vtl.javicalendar.R
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.presentation.model.OptionItem
import com.vtl.javicalendar.presentation.model.ZodiacDisplay

@Composable
fun SettingsSection(option: Option, onOptionChanged: (Option) -> Unit) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
    Text(
        text = stringResource(R.string.settings_title),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 24.dp),
    )

    // Day Detail Section
    SettingsGroup(title = stringResource(R.string.settings_section_day_detail)) {
      OptionItemSettings(
          item = option.dayDetail,
          onItemChanged = { onOptionChanged(option.copy(dayDetail = it)) },
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Month Section
    SettingsGroup(title = stringResource(R.string.settings_section_month)) {
      OptionItemSettings(
          item = option.month,
          onItemChanged = { onOptionChanged(option.copy(month = it)) },
      )
      SettingSwitchItem(
          label = stringResource(R.string.settings_sunday_first),
          checked = option.sundayFirst,
          onCheckedChange = { onOptionChanged(option.copy(sundayFirst = it)) },
      )
    }
  }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp),
    )
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) { content() }
    }
  }
}

@Composable
private fun OptionItemSettings(item: OptionItem, onItemChanged: (OptionItem) -> Unit) {
  SettingSwitchItem(
      label = stringResource(R.string.settings_japanese_date),
      checked = item.japaneseDate,
      onCheckedChange = { onItemChanged(item.copy(japaneseDate = it)) },
  )
  SettingSwitchItem(
      label = stringResource(R.string.settings_lunar_date),
      checked = item.lunarDate,
      onCheckedChange = { onItemChanged(item.copy(lunarDate = it)) },
  )
  SettingSwitchItem(
      label = stringResource(R.string.settings_observance),
      checked = item.observance,
      onCheckedChange = { onItemChanged(item.copy(observance = it)) },
  )

  // Zodiac Display Selection
  Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
        text = stringResource(R.string.settings_zodiac),
        style = MaterialTheme.typography.bodyLarge,
    )
    ZodiacSelector(
        selected = item.zodiac,
        onSelected = { onItemChanged(item.copy(zodiac = it)) },
    )
  }
}

@Composable
private fun ZodiacSelector(selected: ZodiacDisplay, onSelected: (ZodiacDisplay) -> Unit) {
  var expanded by remember { mutableStateOf(false) }

  Box {
    TextButton(onClick = { expanded = true }) { Text(text = getZodiacDisplayName(selected)) }
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
