package com.vtl.javicalendar.presentation.model

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

@Serializable
data class Option(
    val japaneseInfo: Boolean = true,
    val lucDieu: Boolean = true,
    val observance: Boolean = true,
    val monthLucDieu: Boolean = true,
    val monthJapaneseHoliday: Boolean = true,
    val monthObservance: Boolean = true,
) {
  fun adjustBySize(widgetSize: DpSize) =
      copy(
          lucDieu = lucDieu && widgetSize.width >= 250.dp && widgetSize.height >= 400.dp,
          monthLucDieu = monthLucDieu && widgetSize.width >= 250.dp && widgetSize.height >= 400.dp,
          monthJapaneseHoliday =
              monthJapaneseHoliday && widgetSize.width >= 250.dp && widgetSize.height >= 300.dp,
          monthObservance =
              monthObservance && widgetSize.width >= 250.dp && widgetSize.height >= 400.dp,
      )
}
