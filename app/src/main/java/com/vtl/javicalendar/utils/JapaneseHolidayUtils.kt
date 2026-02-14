package com.vtl.javicalendar.utils

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Japanese Holiday Utility
 * * This object calculates Japanese public holidays based on the official guidelines provided by
 *   the Cabinet Office (Cao) of Japan: https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html
 * * Logic follows the current legal framework effective as of 2023/02/23 (The establishment of the
 *   current Emperor's Birthday).
 * * Note: This implementation focuses on the latest permanent laws to predict future holidays and
 *   may intentionally ignore historical one-time exceptions (e.g., 2020-2021 Olympic shifts).
 */
object JapaneseHolidayUtils {

  fun getHolidayName(date: LocalDate): String? {
    val basicHoliday = getBasicHolidayName(date)
    if (basicHoliday != null) return basicHoliday

    val substituteFor = findSubstituteHoliday(date)
    if (substituteFor != null) return "振替休日 ($substituteFor)"

    if (isBetweenHolidays(date)) return "休日"

    return null
  }

  private fun getBasicHolidayName(date: LocalDate): String? {
    return getDynamicHoliday(date) ?: getFixedHoliday(date)
  }

  private fun getFixedHoliday(date: LocalDate): String? {
    val md = date.monthValue * 100 + date.dayOfMonth
    return when (md) {
      101 -> "元日"
      211 -> "建国記念の日"
      223 -> "天皇誕生日"
      429 -> "昭和の日"
      503 -> "憲法記念日"
      504 -> "みどりの日"
      505 -> "こどもの日"
      811 -> "山の日"
      1103 -> "文化の日"
      1123 -> "勤労感謝の日"
      else -> null
    }
  }

  private fun getDynamicHoliday(date: LocalDate): String? {
    val y = date.year
    val m = date.monthValue
    val d = date.dayOfMonth

    return when (m) {
      1 -> if (isHappyMonday(date, 2)) "成人の日" else null
      3 -> if (d == calculateEquinox(y, "Spring")) "春分の日" else null
      7 -> if (isHappyMonday(date, 3)) "海の日" else null
      9 -> {
        val autumnEquinox = calculateEquinox(y, "Autumn")
        if (d == autumnEquinox) "秋分の日" else if (isHappyMonday(date, 3)) "敬老の日" else null
      }
      10 -> if (isHappyMonday(date, 2)) "スポーツの日" else null
      else -> null
    }
  }

  private fun findSubstituteHoliday(date: LocalDate): String? {
    if (date.dayOfWeek == DayOfWeek.SUNDAY || getBasicHolidayName(date) != null) return null

    var checkDate = date.minusDays(1)
    while (getBasicHolidayName(checkDate) != null) {
      if (checkDate.dayOfWeek == DayOfWeek.SUNDAY) {
        return getBasicHolidayName(checkDate)
      }
      checkDate = checkDate.minusDays(1)
    }
    return null
  }

  private fun isHappyMonday(date: LocalDate, week: Int): Boolean {
    return date.dayOfWeek == DayOfWeek.MONDAY && ((date.dayOfMonth - 1) / 7 + 1) == week
  }

  private fun isBetweenHolidays(date: LocalDate): Boolean {
    if (date.dayOfWeek == DayOfWeek.SUNDAY) return false
    val before = getBasicHolidayName(date.minusDays(1))
    val after = getBasicHolidayName(date.plusDays(1))
    return before != null && after != null
  }

  private fun calculateEquinox(year: Int, type: String): Int {
    val factor = if (type == "Spring") 20.8431 else 23.2488
    return (factor + 0.242194 * (year - 1980) - ((year - 1980) / 4)).toInt()
  }

  fun getLongNameForHoliday(originName: String?, localDate: LocalDate): String? {
    if (originName != "休日") return originName
    val holidayName = getHolidayName(localDate) ?: return originName
    return if (holidayName.startsWith("振替休日")) holidayName else originName
  }
}
