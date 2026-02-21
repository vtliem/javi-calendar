package com.vtl.javicalendar.presentation.home.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@Composable
fun LimitFontScale(maxScale: Float, content: @Composable () -> Unit) {
  val currentDensity = LocalDensity.current

  LaunchedEffect(currentDensity.fontScale, maxScale) {
    val appliedScale = minOf(maxScale, currentDensity.fontScale)
    Log.v(
        "FontScaleTrace",
        """
            --- Font Scale Changed ---
            System Scale: ${currentDensity.fontScale}
            Applied Scale (Limited): $appliedScale
            Status: ${if (currentDensity.fontScale > maxScale) "CAPPED" else "NORMAL"}
        """
            .trimIndent(),
    )
  }

  val restrictedDensity =
      Density(
          density = currentDensity.density,
          fontScale = minOf(maxScale, currentDensity.fontScale),
      )

  CompositionLocalProvider(LocalDensity provides restrictedDensity) { content() }
}
