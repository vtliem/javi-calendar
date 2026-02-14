package com.vtl.javicalendar.utils

import com.vtl.javicalendar.domain.model.JapaneseHolidays.Companion.parseHolidays
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.japaneseYear
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JapaneseHolidayUtilsTest {

  companion object {
    private val expectedData by lazy {
      val url = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv"
      (URL(url).openConnection() as HttpURLConnection)
          .apply {
            requestMethod = "GET"
            connectTimeout = 5_000
            readTimeout = 5_000
            connect()
          }
          .let {
            it.inputStream.bufferedReader(Charset.forName("Shift-JIS")).use { reader ->
              parseHolidays(reader.readText())
            }
          }
    }
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd(E)")
  }

  @Test
  fun test() {
    (2023..expectedData.years.last)
        .toList()
        .parallelStream()
        .map {
          var date = LocalDate.of(it, Month.JANUARY, 1)
          val list = mutableListOf<LocalDate>()
          while (date.year <= it) {
            list.add(date)
            date = date.plusDays(1)
          }
          val diff =
              list.mapNotNull { d ->
                val actual = JapaneseHolidayUtils.getHolidayName(d)
                val expected =
                    expectedData.getHoliday(d)?.let {
                      JapaneseHolidayUtils.getLongNameForHoliday(it, d)
                    }
                if (actual != expected) {
                  "\t${d.format(formatter)} ${date.japaneseYear}: actual=$actual, expected=$expected"
                } else {
                  null
                }
              }
          if (diff.isEmpty()) {
            null
          } else {
            "$it\n${diff.joinToString("\n")}"
          }
        }
        .filter { it != null }
        .toList()
        .joinToString("\n")
        .let {
          println(it)
          assertTrue(it.isEmpty(), it.count { it == '\n' }.toString())
        }
  }
}
