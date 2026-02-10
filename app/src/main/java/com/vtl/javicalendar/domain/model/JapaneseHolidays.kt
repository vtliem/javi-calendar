package com.vtl.javicalendar.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class JapaneseHolidays(private val holidayMap: Map<String, String>) {

  fun getHoliday(year: Int, month: Int, day: Int): String? {
    return holidayMap["$year/$month/$day"]
  }

  fun hasData(year: Int): Boolean {
    val prefix = "$year/"
    return holidayMap.keys.any { it.startsWith(prefix) }
  }

  companion object {
    fun parseHolidays(csv: String): JapaneseHolidays {
      val result = mutableMapOf<String, String>()
      val lines = csv.split("\n")
      if (lines.size < 2) return JapaneseHolidays(result)
      for (i in 1 until lines.size) {
        val line = lines[i].trim()
        if (line.isEmpty()) continue
        val parts = line.split(",")
        if (parts.size >= 2) {
          val rawDate = parts[0].trim()
          val normalizedKey =
              try {
                val dateParts = rawDate.split("/")
                if (dateParts.size == 3) {
                  // Normalize to "yyyy/m/d" by parsing parts as integers
                  "${dateParts[0].toInt()}/${dateParts[1].toInt()}/${dateParts[2].toInt()}"
                } else {
                  rawDate
                }
              } catch (_: Exception) {
                rawDate
              }
          result[normalizedKey] = parts[1].trim()
        }
      }
      return JapaneseHolidays(result)
    }
  }
}
