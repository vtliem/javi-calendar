package com.vtl.javicalendar.presentation.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vtl.javicalendar.R
import com.vtl.javicalendar.data.datasource.HolidayErrorType

@Composable
fun HolidayErrorIndicator(error: HolidayErrorType, modifier: Modifier = Modifier) {
  val (icon, color, message) =
      when (error) {
        HolidayErrorType.Network ->
            Triple(
                Icons.Default.Warning,
                MaterialTheme.colorScheme.tertiary,
                stringResource(R.string.error_network),
            )
        else ->
            Triple(
                Icons.Default.Error,
                MaterialTheme.colorScheme.error,
                stringResource(R.string.error_generic, error.name),
            )
      }

  Icon(
      imageVector = icon,
      contentDescription = message,
      tint = color,
      modifier = modifier.size(20.dp).padding(2.dp),
  )
}
