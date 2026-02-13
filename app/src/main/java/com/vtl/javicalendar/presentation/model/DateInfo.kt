package com.vtl.javicalendar.presentation.model

import com.vtl.javicalendar.domain.model.LunarDate
import com.vtl.javicalendar.domain.model.Zodiac
import com.vtl.javicalendar.presentation.theme.Auspicious
import com.vtl.javicalendar.presentation.theme.AuspiciousHours
import com.vtl.javicalendar.presentation.theme.HolidayBlue
import com.vtl.javicalendar.presentation.theme.HolidayRed
import com.vtl.javicalendar.presentation.theme.NoDataJapaneseYear
import com.vtl.javicalendar.presentation.theme.Observance
import com.vtl.javicalendar.presentation.theme.SelectedBorder
import com.vtl.javicalendar.presentation.theme.TodayBackground
import com.vtl.javicalendar.utils.LunarCalendarUtils.convertSolarToLunar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.chrono.JapaneseDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/** Presentation model for displaying a single date with all formatted strings and colors */
data class DateInfo(
    // Original date value for handling clicks and logic
    val value: LocalDate,
    val japaneseHoliday: String?,
    val hasHolidayDataForOfYear: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
) {
  companion object {
    val DayOfWeek.color
      get() =
          when (this) {
            DayOfWeek.SUNDAY -> HolidayRed
            DayOfWeek.SATURDAY -> HolidayBlue
            else -> null
          }

    val DayOfWeek.displayName: String
      get() = getDisplayName(TextStyle.FULL, Locale.getDefault())

    val DayOfWeek.shortName: String
      get() = getDisplayName(TextStyle.SHORT, Locale.getDefault())

    fun colorOfJapaneseYear(hasHolidayDataForOfYear: Boolean) =
        if (!hasHolidayDataForOfYear) NoDataJapaneseYear else null

    val Zodiac.color
      get() = if (this.isAuspicious) Auspicious else null

    private val MonthFormatter = DateTimeFormatter.ofPattern("MMMM")
    val LocalDate.monthName: String
      get() = MonthFormatter.format(this)

    val LocalDate.japaneseYear: String
      get() {
        val jpDate = JapaneseDate.from(this)
        val era = jpDate.era.getDisplayName(TextStyle.FULL, Locale.JAPAN)
        val eraYear = jpDate.get(java.time.temporal.ChronoField.YEAR_OF_ERA)
        return "$era $eraYear"
      }

    val LocalDate.lunarDate: LunarDate
      get() = convertSolarToLunar(dayOfMonth, monthValue, year)
  }

  val japaneseYear by lazy { value.japaneseYear }
  val colorOfJapaneseYear
    get() = colorOfJapaneseYear(hasHolidayDataForOfYear)

  val lunarDate by lazy { convertSolarToLunar(value.dayOfMonth, value.monthValue, value.year) }
  val lunarYear
    get() =
        if (lunarDate.year.value != value.year) lunarDate.year.displayName
        else lunarDate.year.shortName

  val lunarDayOfMonth
    get() =
        if (lunarDate.day.value == 1) "${lunarDate.day.value}/${lunarDate.month.value}"
        else lunarDate.day.value.toString()

  val colorOfDay
    get() = if (japaneseHoliday != null) HolidayRed else value.dayOfWeek.color

  val backgroundColorOfDay
    get() = if (isToday) TodayBackground else null

  val border
    get() = if (isSelected) SelectedBorder else null

  val colorOfAuspiciousHours
    get() = AuspiciousHours

  val colorOfObservance
    get() = Observance

  val colorOfJapaneseHoliday
    get() = HolidayRed

  fun colorOfLunarDay(display: ZodiacDisplay) =
      if (lunarDate.zodiac.isAuspicious && display == ZodiacDisplay.Short) lunarDate.zodiac.color
      else null

  fun hasAdditionalData(option: OptionItem) =
      option.zodiac == ZodiacDisplay.Full ||
          (option.japaneseDate && japaneseHoliday != null) ||
          (option.observance && lunarDate.observance != null)
}
