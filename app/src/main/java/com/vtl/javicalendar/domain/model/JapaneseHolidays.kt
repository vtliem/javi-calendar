package com.vtl.javicalendar.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class JapaneseHolidays(private val holidayMap: Map<Int, Map<String, String>>) {

  fun getHoliday(year: Int, month: Int, day: Int): String? {
    return holidayMap[year]?.get("$month/$day")
  }

  fun hasData(year: Int) = holidayMap.containsKey(year)

  companion object {
    private fun parseDate(date: String): Triple<Int, Int, Int>? {
      val parts = date.split("/")
      if (parts.size != 3) return null
      val year = parts[0].trim().toIntOrNull() ?: return null
      val month = parts[1].trim().removePrefix("0").toIntOrNull() ?: return null
      val day = parts[2].trim().removePrefix("0").toIntOrNull() ?: return null
      return Triple(year, month, day)
    }

    fun parseHolidays(csv: String) =
        csv.lineSequence()
            .mapNotNull {
              val parts = it.split(",")
              if (parts.size < 2) return@mapNotNull null
              val date = parseDate(parts[0]) ?: return@mapNotNull null
              date to parts[1].trim()
            }
            .groupBy({ it.first.first }, { "${it.first.second}/${it.first.third}" to it.second })
            .mapValues { it.value.toMap() }
            .let { JapaneseHolidays(it) }
  }
}
