package com.vtl.holidaycalendar.domain.model

class JapaneseHolidays(private val holidayMap: Map<String, String>) {
    
    fun getHoliday(year: Int, month: Int, day: Int): String? {
        val dateKey = String.format("%04d/%d/%d", year, month, day)
        return holidayMap[dateKey] ?: holidayMap["$year/%02d/%02d".format(month, day)] ?: holidayMap["$year/$month/$day"]
    }

    fun getAllHolidays(): Map<String, String> = holidayMap

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
                    result[parts[0].trim()] = parts[1].trim()
                }
            }
            return JapaneseHolidays(result)
        }
    }
}
