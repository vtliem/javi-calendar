package com.vtl.javicalendar.presentation.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

/** Presentation model for displaying a single date with all formatted strings and colors */
data class DateInfo(
    // Original date value for handling clicks and logic
    val value: LocalDate,

    // Gregorian date display
    val year: DisplayField,
    val month: DisplayField,
    val day: DisplayField,
    val weekday: DisplayField,
    val fullDisplayDay: DisplayField?,

    // Japanese date
    val japaneseDate: JapaneseDateDisplay,

    // Lunar date
    val lunarDate: LunarDateDisplay,
)

data class DisplayField(val value: String, val color: Color, val backgroundColor: Color? = null)

data class JapaneseDateDisplay(val year: DisplayField?, val holiday: DisplayField?)

data class LunarDateDisplay(
    val year: DisplayField?,
    val month: DisplayField?,
    val day: DisplayField?,
    val fullDisplayDay: DisplayField?,
    val observance: DisplayField?,
    val lucDieu: DisplayField?,
    val lucDieuFullDisplay: DisplayField?,
    val auspiciousHours: DisplayField?,
)
