package com.vtl.javicalendar.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceTheme
import androidx.glance.color.ColorProvider

@Composable
fun widgetColor(color: Color, isSecondary: Boolean = false) =
    if (color.alpha == 0f) {
      if (isSecondary) GlanceTheme.colors.onSurfaceVariant else GlanceTheme.colors.onSurface
    } else {
      ColorProvider(day = color, night = color)
    }
