package com.vtl.javicalendar.domain.model

import com.vtl.javicalendar.presentation.model.DateInfo.Companion.lunarDate
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class DutyTest {
  @ParameterizedTest
  @DisplayName("The duty of a lunar date should be correct")
  @CsvSource(
      "2026-02-13, Định",
      "2026-02-14, Chấp",
      "2026-02-15, Phá",
      "2026-02-16, Nguy",
      "2026-02-17, Thành",
      "2026-02-18, Thu",
      "2026-02-19, Khai",
      "2026-02-20, Bế",
      "2026-02-21, Kiến",
      "2026-02-22, Trừ",
      "2026-02-23, Mãn",
  )
  fun of(date: String, expected: String) {
    val localDate = LocalDate.parse(date)
    val actual = localDate.lunarDate.duty.dutyName
    assertEquals(expected, actual, date)
  }
}
