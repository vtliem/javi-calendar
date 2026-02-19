package com.vtl.javicalendar.domain

import com.vtl.javicalendar.domain.model.JapaneseHolidays
import com.vtl.javicalendar.presentation.model.DateInfo
import com.vtl.javicalendar.presentation.model.MonthInfo
import com.vtl.javicalendar.utils.JapaneseHolidayUtils
import java.time.DayOfWeek
import java.time.LocalDate

object CalendarFactory {

  fun createMonthInfo(
      sundayFist: Boolean,
      year: Int,
      month: Int,
      holidays: JapaneseHolidays,
      selectedDate: LocalDate? = null,
      today: LocalDate = LocalDate.now(),
  ): MonthInfo {
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val daysInMonth = firstDayOfMonth.lengthOfMonth()

    val daysOfWeek =
        if (sundayFist) {
          listOf(DayOfWeek.SUNDAY) + DayOfWeek.entries.filterNot { it == DayOfWeek.SUNDAY }
        } else DayOfWeek.entries

    // Calculate grid layout
    val leadingEmptySlots =
        if (sundayFist) {
          if (firstDayOfMonth.dayOfWeek == DayOfWeek.SUNDAY) 0 else firstDayOfMonth.dayOfWeek.value
        } else {
          firstDayOfMonth.dayOfWeek.value - 1
        }
    val numOfWeeks = (daysInMonth + leadingEmptySlots + 6) / 7

    val weeks =
        (0 until (numOfWeeks * 7))
            .map {
              val day = it - leadingEmptySlots + 1
              if (day in 1..daysInMonth) {
                val date = LocalDate.of(year, month, day)
                createDateInfo(date, holidays, selectedDate, today)
              } else {
                null
              }
            }
            .chunked(7)
    return MonthInfo(daysOfWeek, weeks)
  }

  /** Create a DateInfo with all formatting and colors */
  private fun createDateInfo(
      date: LocalDate,
      holidays: JapaneseHolidays,
      selectedDate: LocalDate?,
      today: LocalDate,
  ): DateInfo {
    // Get Japanese holiday
    val (japaneseHoliday, japaneseLongHoliday) =
        if (holidays.hasData(date.year)) {
          val japaneseHoliday = holidays.getHoliday(date)
          val japaneseLongHoliday =
              JapaneseHolidayUtils.getLongNameForHoliday(japaneseHoliday, date)
          Pair(japaneseHoliday, japaneseLongHoliday)
        } else {
          val japaneseHoliday = JapaneseHolidayUtils.getHolidayName(date)
          Pair(japaneseHoliday, japaneseHoliday)
        }

    // Determine states
    val isToday = date == today
    val isSelected = date == selectedDate

    return DateInfo(
        value = date,
        japaneseHoliday = japaneseHoliday,
        japaneseLongHoliday = japaneseLongHoliday,
        hasHolidayDataForOfYear = holidays.hasData(date.year),
        isToday = isToday,
        isSelected = isSelected,
    )
  }
}
