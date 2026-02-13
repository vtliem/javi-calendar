package com.vtl.javicalendar.presentation.model

import java.time.DayOfWeek
import java.time.LocalDate

/** Presentation model for displaying a month with all formatted strings and colors */
data class MonthInfo(
    val daysOfWeek: List<DayOfWeek>,
    // Grid of dates
    val weeks: List<List<DateInfo?>>,
) {
  /** Find DateInfo for a specific date */
  fun getDate(localDate: LocalDate): DateInfo? {
    return weeks.flatten().find { it?.value == localDate }
  }
}
