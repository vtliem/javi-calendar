package com.vtl.javicalendar.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vtl.javicalendar.presentation.model.Option

fun Int.limitScaleSp(option: Option, fontScale: Float): TextUnit {
  if (fontScale <= option.maxFontScale) return this.sp
  return (this * option.maxFontScale / fontScale).sp
}

fun Int.scaleDp(option: Option, fontScale: Float): Dp {
  return (minOf(option.maxFontScale, fontScale) * this).dp
}
