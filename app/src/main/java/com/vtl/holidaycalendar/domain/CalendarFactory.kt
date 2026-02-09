package com.vtl.holidaycalendar.domain

import androidx.compose.ui.graphics.Color
import com.vtl.holidaycalendar.domain.model.JapaneseHolidays
import com.vtl.holidaycalendar.presentation.model.*
import com.vtl.holidaycalendar.presentation.theme.HolidayBlue
import com.vtl.holidaycalendar.presentation.theme.HolidayOrange
import com.vtl.holidaycalendar.presentation.theme.HolidayRed
import com.vtl.holidaycalendar.utils.LunarCalendarUtils
import com.vtl.holidaycalendar.utils.LunarCalendarUtils.lunarMonthName
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/**
 * Factory for creating presentation models with all formatting and colors
 */
object CalendarFactory {

    private fun getLocale(): Locale = Locale.getDefault()

    /**
     * Create a MonthInfo with all dates formatted and colored
     */
    fun createMonthInfo(
        year: Int,
        month: Int,
        holidays: JapaneseHolidays,
        selectedDate: LocalDate? = null,
        today: LocalDate = LocalDate.now()
    ): MonthInfo {
        val locale = getLocale()
        val firstDayOfMonth = LocalDate.of(year, month, 1)
        val daysInMonth = firstDayOfMonth.lengthOfMonth()
        
        // Calculate grid layout
        val firstDayOfWeekValue = firstDayOfMonth.dayOfWeek.value
        val leadingEmptySlots = if (firstDayOfWeekValue == 7) 0 else firstDayOfWeekValue

        val weeks = mutableListOf<List<DateInfo?>>()
        var currentWeek = mutableListOf<DateInfo?>()

        // Fill leading empty slots
        for (i in 0 until leadingEmptySlots) {
            currentWeek.add(null)
        }

        // Fill dates
        for (day in 1..daysInMonth) {
            val date = LocalDate.of(year, month, day)
            currentWeek.add(createDateInfo(date, holidays, selectedDate, today))

            if (currentWeek.size == 7) {
                weeks.add(currentWeek)
                currentWeek = mutableListOf()
            }
        }

        // Fill trailing empty slots
        if (currentWeek.isNotEmpty()) {
            while (currentWeek.size < 7) {
                currentWeek.add(null)
            }
            weeks.add(currentWeek)
        }

        // Week day names with colors
        val weekDayNames = (0..6).map { i ->
            val dayOfWeek = if (i == 0) DayOfWeek.SUNDAY else DayOfWeek.of(i)
            val name = dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
            val color = when (i) {
                0 -> HolidayRed // Sunday
                6 -> HolidayBlue // Saturday
                else -> Color.Unspecified
            }
            DisplayField(value = name, color = color)
        }

        return MonthInfo(
            weekDayNames = weekDayNames,
            weeks = weeks
        )
    }

    /**
     * Create a DateInfo with all formatting and colors
     */
    private fun createDateInfo(
        date: LocalDate,
        holidays: JapaneseHolidays,
        selectedDate: LocalDate?,
        today: LocalDate
    ): DateInfo {
        val locale = getLocale()
        
        // Get lunar date
        val lunarDate = LunarCalendarUtils.convertSolarToLunar(
            date.dayOfMonth,
            date.monthValue,
            date.year
        )
        
        // Get Japanese holiday
        val japaneseHoliday = holidays.getHoliday(date.year, date.monthValue, date.dayOfMonth)
        
        // Determine states
        val isToday = date == today
        val isSelected = date == selectedDate
        val isSunday = date.dayOfWeek == DayOfWeek.SUNDAY
        val isSaturday = date.dayOfWeek == DayOfWeek.SATURDAY
        val hasHoliday = japaneseHoliday != null
        
        // Determine colors
        val dateColor = when {
            isSunday || hasHoliday -> HolidayRed
            isSaturday -> HolidayBlue
            else -> Color.Unspecified
        }
        
        val dayColor = when {
            isSunday -> HolidayRed
            isSaturday -> HolidayBlue
            else -> Color.Unspecified
        }
        
        val backgroundColor = when {
            isSelected -> Color.Gray.copy(alpha = 0.3f)
            isToday -> {
                when {
                    isSunday || hasHoliday -> HolidayRed
                    isSaturday -> HolidayBlue
                    else -> Color.Gray // Or some default "today" indicator
                }
            }
            else -> null
        }
        
        // Japanese Year
        val japaneseYear = try {
            val jpDate = java.time.chrono.JapaneseDate.from(date)
            val era = jpDate.era.getDisplayName(TextStyle.FULL, Locale.JAPAN)
            val eraYear = jpDate.get(java.time.temporal.ChronoField.YEAR_OF_ERA)
            "$era $eraYear"
        } catch (e: Exception) {
            if (locale.language == "vi") "Năm ${date.year}" else "Year ${date.year}"
        }
        
        // Lunar year display
        val lunarYearDisplay = if (lunarDate.year != date.year) {
            "${lunarDate.year} - ${lunarDate.yearCanChi}"
        } else {
            lunarDate.yearCanChi
        }
        
        // Month names
        val monthFormatter = DateTimeFormatter.ofPattern("MMMM", locale)
        val monthName = date.format(monthFormatter)
        val lunarMonthNameStr = "Tháng ${lunarMonthName(lunarDate.month)} - ${lunarDate.monthCanChi}"
        
        val japaneseYearColor = if (!holidays.hasData(date.year)) HolidayOrange else Color.Unspecified

        return DateInfo(
            value = date,
            
            // Gregorian date
            year = DisplayField(
                value = date.year.toString(),
                color = Color.Unspecified
            ),
            month = DisplayField(
                value = monthName,
                color = Color.Unspecified
            ),
            day = DisplayField(
                value = date.dayOfMonth.toString(),
                color = dateColor,
                backgroundColor = backgroundColor
            ),
            weekday = DisplayField(
                value = date.dayOfWeek.getDisplayName(TextStyle.FULL, locale),
                color = dayColor
            ),
            fullDisplayDay = DisplayField(
                value = date.format(DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.FULL).withLocale(locale)),
                color = Color.Unspecified
            ),
            
            // Japanese date
            japaneseDate = JapaneseDateDisplay(
                year = DisplayField(
                    value = "($japaneseYear)",
                    color = japaneseYearColor
                ),
                holiday = japaneseHoliday?.let {
                    DisplayField(
                        value = it,
                        color = HolidayRed
                    )
                }
            ),
            
            // Lunar date
            lunarDate = LunarDateDisplay(
                year = DisplayField(
                    value = "($lunarYearDisplay)",
                    color = Color.Unspecified
                ),
                month = DisplayField(
                    value = "($lunarMonthNameStr)",
                    color = Color.Unspecified
                ),
                day = DisplayField(
                    value = "${lunarDate.day}${if(lunarDate.day == 1) "/${lunarDate.month}" else ""}",
                    color = Color.Unspecified
                ),
                fullDisplayDay = DisplayField(
                    value = "Ngày ${lunarDate.day} - ${lunarDate.canChi}",
                    color = Color.Unspecified
                ),
                observance = lunarDate.observance?.let {
                    DisplayField(
                        value = it,
                        color = HolidayBlue
                    )
                },
                lucDieu = DisplayField(
                    value = lunarDate.statusLabel,
                    color = if (lunarDate.isAuspicious) HolidayOrange else Color.Unspecified
                ),
                lucDieuFullDisplay = DisplayField(
                    value = "${lunarDate.statusPrefix}: ${lunarDate.statusLabel}",
                    color = if (lunarDate.isAuspicious) HolidayOrange else Color.Unspecified
                ),
                auspiciousHours = DisplayField(
                    value = lunarDate.auspiciousHours,
                    color = Color.Unspecified
                )
            )
        )
    }
}
