package com.vtl.javicalendar.presentation.model

import java.time.LocalDate
import kotlinx.serialization.Serializable

/**
 * Presentation model for displaying a month with all formatted strings and colors
 */
@Serializable
data class MonthInfo(
    // Week day names (Sun, Mon, ...)
    val weekDayNames: List<DisplayField>,
    
    // Grid of dates
    val weeks: List<List<DateInfo?>>
) {
    /**
     * Find DateInfo for a specific date
     */
    fun getDate(localDate: LocalDate): DateInfo? {
        return weeks.flatten().filterNotNull().find { it.value == localDate }
    }
}
