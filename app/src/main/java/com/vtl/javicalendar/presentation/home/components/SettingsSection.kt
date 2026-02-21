package com.vtl.javicalendar.presentation.home.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.vtl.javicalendar.R
import com.vtl.javicalendar.data.datasource.HolidayRemoteDataSource
import com.vtl.javicalendar.domain.model.JapaneseHolidays
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.presentation.model.ZodiacDisplay
import com.vtl.javicalendar.presentation.theme.NoDataJapaneseYear
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSection(
    option: Option,
    holidays: JapaneseHolidays,
    onOptionChanged: (Option) -> Unit,
    onSyncClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
) {
  val uriHandler = LocalUriHandler.current

  if (onBackClick != null) {
    BackHandler(onBack = onBackClick)
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text(text = stringResource(R.string.settings_title)) },
            navigationIcon = {
              if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                  Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
              }
            },
        )
      }
  ) { innerPadding ->
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
    ) {
      Spacer(modifier = Modifier.height(8.dp))

      // Limit font scale slider
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
              text = stringResource(R.string.settings_max_font_scale),
              style = MaterialTheme.typography.bodyLarge,
          )
          Text(
              text = "%.1f".format(option.maxFontScale),
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(start = 8.dp),
          )
        }
        Slider(
            value = option.maxFontScale,
            onValueChange = {
              val rounded = (it * 10).roundToInt() / 10f
              onOptionChanged(option.copy(maxFontScale = rounded))
            },
            valueRange = 1f..2f,
            steps = 9,
        )
      }
      Spacer(modifier = Modifier.height(32.dp))

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

      Spacer(modifier = Modifier.height(32.dp))

      // Holiday Data Sync Section
      Text(
          text = stringResource(R.string.settings_holiday_sync_title),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(bottom = 4.dp),
      )

      val noteTemplate = stringResource(R.string.settings_holiday_no_data_note_template)
      val orangeWord = stringResource(R.string.settings_holiday_orange_word)
      val exampleYear = "2026"

      val annotatedNote =
          remember(noteTemplate, orangeWord, exampleYear) {
            buildAnnotatedString {
              val orangeIndex = noteTemplate.indexOf($$"%1$s")
              val yearIndex = noteTemplate.indexOf($$"%2$s")

              if (orangeIndex != -1 && yearIndex != -1) {
                if (orangeIndex < yearIndex) {
                  append(noteTemplate.substring(0, orangeIndex))
                  withStyle(SpanStyle(color = NoDataJapaneseYear, fontWeight = FontWeight.Bold)) {
                    append(orangeWord)
                  }
                  append(noteTemplate.substring(orangeIndex + 4, yearIndex))
                  withStyle(SpanStyle(color = NoDataJapaneseYear, fontWeight = FontWeight.Bold)) {
                    append(exampleYear)
                  }
                  append(noteTemplate.substring(yearIndex + 4))
                } else {
                  append(noteTemplate.substring(0, yearIndex))
                  withStyle(SpanStyle(color = NoDataJapaneseYear, fontWeight = FontWeight.Bold)) {
                    append(exampleYear)
                  }
                  append(noteTemplate.substring(yearIndex + 4, orangeIndex))
                  withStyle(SpanStyle(color = NoDataJapaneseYear, fontWeight = FontWeight.Bold)) {
                    append(exampleYear)
                  }
                  append(noteTemplate.substring(orangeIndex + 4))
                }
              } else {
                append(noteTemplate.replace($$"%1$s", orangeWord).replace($$"%2$s", exampleYear))
              }
            }
          }

      Text(
          text = annotatedNote,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(bottom = 8.dp),
      )

      // line 1: [Data Source] sync button [space] [About data]
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
              text = stringResource(R.string.settings_holiday_source),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.primary,
              textDecoration = TextDecoration.Underline,
              modifier = Modifier.clickable { uriHandler.openUri(HolidayRemoteDataSource.CSV_URL) },
          )
          IconButton(onClick = onSyncClick, modifier = Modifier.size(32.dp)) {
            Icon(
                Icons.Default.Sync,
                contentDescription = stringResource(R.string.settings_holiday_sync_button),
                modifier = Modifier.size(18.dp),
            )
          }
        }
        TextButton(
            onClick = { uriHandler.openUri(HolidayRemoteDataSource.INFO_URL) },
            contentPadding = PaddingValues(0.dp),
        ) {
          Text(
              text = stringResource(R.string.settings_holiday_about),
              style = MaterialTheme.typography.bodyMedium,
          )
          Icon(
              Icons.AutoMirrored.Filled.OpenInNew,
              contentDescription = null,
              modifier = Modifier.size(14.dp).padding(start = 4.dp),
          )
        }
      }

      val formatter = remember {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault())
      }

      // line 2: Updated At:xxx [space] Last Synced at:xxx
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
            text =
                stringResource(
                    R.string.settings_holiday_updated_at,
                    if (holidays.lastModified > 0)
                        formatter.format(Instant.ofEpochMilli(holidays.lastModified))
                    else stringResource(R.string.settings_holiday_not_available),
                ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text =
                stringResource(
                    R.string.settings_holiday_last_synced,
                    if (holidays.lastSuccess > 0)
                        formatter.format(Instant.ofEpochMilli(holidays.lastSuccess))
                    else stringResource(R.string.settings_holiday_not_available),
                ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      holidays.error?.let { error ->
        Text(
            text = stringResource(R.string.settings_holiday_error_prefix, error.name),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 4.dp),
        )
      }
      Spacer(modifier = Modifier.height(32.dp))
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
    ZodiacDisplay.ByColor -> stringResource(R.string.zodiac_display_color)
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
