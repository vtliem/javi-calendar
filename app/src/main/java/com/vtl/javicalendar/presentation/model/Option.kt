package com.vtl.javicalendar.presentation.model

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

enum class ZodiacDisplay {
  Full,
  ByColor,
  None,
}

@Serializable
data class OptionItem(
    val japaneseDate: Boolean = true,
    val lunarDate: Boolean = true,
    val zodiac: ZodiacDisplay = ZodiacDisplay.Full,
    val observance: Boolean = true,
) {
  fun adjustBySize(widgetSize: DpSize) =
      copy(
          japaneseDate = japaneseDate && widgetSize.width >= 250.dp,
          zodiac =
              if (zodiac != ZodiacDisplay.Full) zodiac
              else if (widgetSize.width >= 250.dp && widgetSize.height >= 400.dp) zodiac
              else ZodiacDisplay.ByColor,
          observance = observance && widgetSize.width >= 250.dp && widgetSize.height >= 400.dp,
      )
}

@Serializable
data class Option(
    val dayDetail: OptionItem = OptionItem(),
    val month: OptionItem = OptionItem(),
    val sundayFirst: Boolean = true,
    val maxFontScale: Float = 1.5f,
) {
  fun adjustBySize(widgetSize: DpSize) =
      copy(
          month = month.adjustBySize(widgetSize),
          dayDetail = dayDetail.adjustBySize(widgetSize),
      )
}
