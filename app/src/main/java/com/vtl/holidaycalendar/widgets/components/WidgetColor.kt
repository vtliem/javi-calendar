package com.vtl.holidaycalendar.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.luminance
import androidx.glance.GlanceTheme
import androidx.glance.color.ColorProvider

@Composable
fun widgetColor(color: Color) = if(color.isUnspecified) GlanceTheme.colors.primary else ColorProvider(day= color, night=color)
