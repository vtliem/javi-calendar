package com.vtl.javicalendar.domain.model

import com.vtl.javicalendar.data.datasource.HolidayErrorType
import java.time.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class JapaneseHolidays(
    private val holidayMap: Map<Int, String> = emptyMap(),
    private val minYear: Int = 0,
    private val maxYear: Int = 0,
    val lastModified: Long = 0,
    val lastSuccess: Long = 0,
    val error: HolidayErrorType? = null,
) {

  fun getHoliday(date: LocalDate): String? {
    return holidayMap[key(date.year, date.monthValue, date.dayOfMonth)]
  }

  val years by lazy { minYear..maxYear }

  fun hasData(year: Int) = year in years

  companion object {
    private fun key(year: Int, month: Int, day: Int) = year * 10000 + month * 100 + day

    private fun parseDate(date: String): Int? {
      val parts = date.split("/")
      if (parts.size != 3) return null
      val year = parts[0].trim().toIntOrNull() ?: return null
      val month = parts[1].trim().removePrefix("0").toIntOrNull() ?: return null
      val day = parts[2].trim().removePrefix("0").toIntOrNull() ?: return null
      return key(year, month, day)
    }

    fun parseHolidays(
        csv: String,
        lastModified: Long = 0,
        lastSuccess: Long = 0,
        error: HolidayErrorType? = null,
    ) =
        csv.lineSequence()
            .mapNotNull {
              val parts = it.split(",")
              if (parts.size < 2) return@mapNotNull null
              val key = parseDate(parts[0]) ?: return@mapNotNull null
              key to parts[1].trim()
            }
            .let {
              val map = mutableMapOf<Int, String>()
              var minKey = Int.MAX_VALUE
              var maxKey = Int.MIN_VALUE
              it.forEach { (key, value) ->
                map[key] = value
                minKey = minOf(minKey, key)
                maxKey = maxOf(maxKey, key)
              }
              JapaneseHolidays(
                  holidayMap = map,
                  minYear = minKey / 10000,
                  maxYear = maxKey / 10000,
                  lastModified = lastModified,
                  lastSuccess = lastSuccess,
                  error = error,
              )
            }
  }
}
