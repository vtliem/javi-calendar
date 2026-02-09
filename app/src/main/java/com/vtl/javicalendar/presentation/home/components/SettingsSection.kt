package com.vtl.javicalendar.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.R


@Composable
fun SettingsSection(
    option: Option,
    onOptionChanged: (Option) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        SettingSwitchItem(
            label = stringResource(R.string.settings_show_japanese_info_details),
            checked = option.japaneseInfo,
            onCheckedChange = { onOptionChanged(option.copy(japaneseInfo = it)) }
        )

        SettingSwitchItem(
            label = stringResource(R.string.settings_show_luc_dieu_details),
            checked = option.lucDieu,
            onCheckedChange = { onOptionChanged(option.copy(lucDieu = it)) }
        )

        SettingSwitchItem(
            label = stringResource(R.string.settings_show_observances_details),
            checked = option.observance,
            onCheckedChange = { onOptionChanged(option.copy(observance = it)) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        SettingSwitchItem(
            label = stringResource(R.string.settings_show_luc_dieu_grid),
            checked = option.monthLucDieu,
            onCheckedChange = { onOptionChanged(option.copy(monthLucDieu = it)) }
        )

        SettingSwitchItem(
            label = stringResource(R.string.settings_show_japanese_holidays_grid),
            checked = option.monthJapaneseHoliday,
            onCheckedChange = { onOptionChanged(option.copy(monthJapaneseHoliday = it)) }
        )

        SettingSwitchItem(
            label = stringResource(R.string.settings_show_observances_grid),
            checked = option.monthObservance,
            onCheckedChange = { onOptionChanged(option.copy(monthObservance = it)) }
        )
    }
}

@Composable
private fun SettingSwitchItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
