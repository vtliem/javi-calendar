package com.vtl.javicalendar.domain.model

enum class Can(val displayName: String) {
  Giap("Giáp"),
  At("Ất"),
  Binh("Bính"),
  Dinh("Đinh"),
  Mau("Mậu"),
  Ky("Kỷ"),
  Canh("Canh"),
  Tan("Tân"),
  Nham("Nhâm"),
  Quy("Quý");

  override fun toString(): String = displayName

  companion object {
    fun ofYear(lunarYear: Int) = entries[(lunarYear + 6) % 10]

    fun ofMonth(lunarMonth: Int, lunarYear: Int): Can {
      val yearCanIndex = (lunarYear + 6) % 10
      // Công thức Ngũ hổ độn: Can tháng 1 = (Can năm * 2 + 1) % 10
      val startCanOfMonth1 = (yearCanIndex * 2 + 2) % 10
      return entries[(startCanOfMonth1 + (lunarMonth - 1)) % 10]
    }

    fun ofDay(julianDay: Int) = entries[(julianDay + 9) % 10]
  }
}

enum class Chi(val displayName: String) {
  Ty("Tý"),
  Suu("Sửu"),
  Dan("Dần"),
  Mao("Mão"),
  Thin("Thìn"),
  Ty_("Tỵ"),
  Ngo("Ngọ"),
  Mui("Mùi"),
  Than("Thân"),
  Dau("Dậu"),
  Tuat("Tuất"),
  Hoi("Hợi");

  override fun toString(): String = displayName

  companion object {
    fun ofYear(lunarYear: Int) = entries[(lunarYear + 8) % 12]

    fun ofMonth(lunarMonth: Int) = entries[(lunarMonth + 1) % 12]

    fun ofDay(julianDay: Int) = entries[(julianDay + 1) % 12]
  }
}

private val LunarObservances =
    mapOf(
        "1/1" to "Tết Nguyên Đán",
        "2/1" to "Tết Nguyên Đán",
        "3/1" to "Tết Nguyên Đán",
        "15/1" to "Rằm tháng Giêng",
        "3/3" to "Tết Hàn Thực",
        "10/3" to "Giỗ tổ Hùng Vương",
        "15/4" to "Lễ Phật Đản",
        "5/5" to "Tết Đoan Ngọ",
        "15/7" to "Rằm tháng Bảy",
        "15/8" to "Tết Trung Thu",
        "23/12" to "Tiễn Táo Quân về trời",
    )

sealed interface LunarDateField {
  val value: Int
  val can: Can
  val chi: Chi
  val displayName: String
}

data class LunarDay(
    override val value: Int,
    override val can: Can,
    override val chi: Chi,
) : LunarDateField {
  override val displayName: String
    get() = "Ngày $value - $can $chi"
}

data class LunarMonth(
    override val value: Int,
    override val can: Can,
    override val chi: Chi,
    val isLeap: Boolean,
) : LunarDateField {
  val monthName: String
    get() =
        when (value) {
          1 -> "Giêng"
          11 -> "Một"
          12 -> "Chạp"
          else -> value.toString()
        }

  override val displayName: String
    get() = "Tháng $monthName - $can $chi${if(isLeap) " - Nhuận" else ""}"
}

data class LunarYear(
    override val value: Int,
    override val can: Can,
    override val chi: Chi,
    val leapMonth: Int?,
) : LunarDateField {
  val shortName
    get() =
        "$can $chi${leapMonth?.let { " - Nhuận(${LunarMonth(it, can, chi, false).monthName})" } ?: ""}"

  override val displayName: String
    get() = "$value - $shortName"
}

data class LunarDate(
    private val lunarYear: Int,
    private val lunarMonth: Int,
    private val lunarDay: Int,
    val leapMonth: Int?,
    val isLeapMonth: Boolean,
    val julianDay: Int,
    val solarTerm: Int,
    val solarTermMonthChi: Int,
) {
  val year by lazy { LunarYear(lunarYear, Can.ofYear(lunarYear), Chi.ofYear(lunarYear), leapMonth) }

  val month by lazy {
    LunarMonth(lunarMonth, Can.ofMonth(lunarMonth, lunarYear), Chi.ofMonth(lunarMonth), isLeapMonth)
  }
  val day by lazy { LunarDay(lunarDay, Can.ofDay(julianDay), Chi.ofDay(julianDay)) }
  val observance
    get() = LunarObservances["$lunarDay/$lunarMonth"]

  val zodiac
    get() = Zodiac.of(month, day)

  val duty
    get() = Duty.of(solarTermMonthChi, day)

  val solarTermName
    get() = getSolarTermName(solarTerm)

  val auspiciousHours
    get() = calcAuspiciousHours(day).joinToString(", ")
}
