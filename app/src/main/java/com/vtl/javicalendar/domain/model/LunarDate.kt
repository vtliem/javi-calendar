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

enum class Zodiac(
    val zodiacName: String,
    val isAuspicious: Boolean,
    val detail: String,
) {
  ThanhLong("Thanh Long", true, "Tốt cho cưới hỏi, khai trương, thi cử và các việc hỷ sự."),
  MinhDuong("Minh Đường", true, "Tốt cho nhập học, khai trương, nhậm chức, giao thương, động thổ."),
  ThienHinh(
      "Thiên Hình",
      false,
      "Rất xấu cho cưới hỏi, xây dựng, nhậm chức, nhập học, khai trương, mua xe, mua nhà.",
  ),
  ChuTuoc(
      "Chu Tước",
      false,
      "Rất xấu cho khai trương, mở xưởng, nhập trạch và các việc cầu tài lộc.",
  ),
  KimQuy("Kim Quỹ", true, "Lý tưởng cho hôn sự, giao tiếp, thỏa thuận, hội họp và tranh biện."),
  KimDuong("Bảo Quang", true, "Tốt cho khởi công, động thổ, khai trương, cưới hỏi, nhậm chức."),
  BachHo(
      "Bạch Hổ",
      false,
      "Rất xấu cho mọi việc, tối kỵ mai táng và các việc liên quan đến đất đai.",
  ),
  NgocDuong(
      "Ngọc Đường",
      true,
      "Rất tốt cho thi cử, khai trương, động thổ, nhậm chức và phát triển tài năng.",
  ),
  ThienLao("Thiên Lao", false, "Xấu cho động thổ, nhập trạch, xuất hành, chữa bệnh, cưới hỏi."),
  NguyenVu(
      "Huyền Vũ",
      false,
      "Rất xấu cho cưới hỏi, làm nhà, nhập trạch, ký kết, khai trương, nhậm chức.",
  ),
  TuMenh("Tư Mệnh", true, "Rất tốt cho khai trương, động thổ, ký kết hợp đồng, cưới hỏi."),
  CauTran(
      "Câu Trận",
      false,
      "Rất xấu cho cưới hỏi, động thổ, đổ mái, xuất hành, tu tạo, tế tự, chữa bệnh.",
  );

  val typeName
    get() = if (isAuspicious) "Hoàng Đạo" else "Hắc Đạo"

  override fun toString() = "$zodiacName $typeName"

  companion object {
    fun of(
        month: LunarMonth,
        day: LunarDay,
    ): Zodiac {
      return when (month.value) {
        1,
        7 ->
            when (day.chi) {
              Chi.Ty -> ThanhLong
              Chi.Suu -> MinhDuong
              Chi.Dan -> ThienHinh
              Chi.Mao -> ChuTuoc
              Chi.Thin -> KimQuy
              Chi.Ty_ -> KimDuong
              Chi.Ngo -> BachHo
              Chi.Mui -> NgocDuong
              Chi.Than -> ThienLao
              Chi.Dau -> NguyenVu
              Chi.Tuat -> TuMenh
              Chi.Hoi -> CauTran
            }

        2,
        8 ->
            when (day.chi) {
              Chi.Dan -> ThanhLong
              Chi.Mao -> MinhDuong
              Chi.Thin -> ThienHinh
              Chi.Ty_ -> ChuTuoc
              Chi.Ngo -> KimQuy
              Chi.Mui -> KimDuong
              Chi.Than -> BachHo
              Chi.Dau -> NgocDuong
              Chi.Tuat -> ThienLao
              Chi.Hoi -> NguyenVu
              Chi.Ty -> TuMenh
              Chi.Suu -> CauTran
            }

        3,
        9 ->
            when (day.chi) {
              Chi.Thin -> ThanhLong
              Chi.Ty_ -> MinhDuong
              Chi.Ngo -> ThienHinh
              Chi.Mui -> ChuTuoc
              Chi.Than -> KimQuy
              Chi.Dau -> KimDuong
              Chi.Tuat -> BachHo
              Chi.Hoi -> NgocDuong
              Chi.Ty -> ThienLao
              Chi.Suu -> NguyenVu
              Chi.Dan -> TuMenh
              Chi.Mao -> CauTran
            }

        4,
        10 ->
            when (day.chi) {
              Chi.Ngo -> ThanhLong
              Chi.Mui -> MinhDuong
              Chi.Than -> ThienHinh
              Chi.Dau -> ChuTuoc
              Chi.Tuat -> KimQuy
              Chi.Hoi -> KimDuong
              Chi.Ty -> BachHo
              Chi.Suu -> NgocDuong
              Chi.Dan -> ThienLao
              Chi.Mao -> NguyenVu
              Chi.Thin -> TuMenh
              Chi.Ty_ -> CauTran
            }

        5,
        11 ->
            when (day.chi) {
              Chi.Than -> ThanhLong
              Chi.Dau -> MinhDuong
              Chi.Tuat -> ThienHinh
              Chi.Hoi -> ChuTuoc
              Chi.Ty -> KimQuy
              Chi.Suu -> KimDuong
              Chi.Dan -> BachHo
              Chi.Mao -> NgocDuong
              Chi.Thin -> ThienLao
              Chi.Ty_ -> NguyenVu
              Chi.Ngo -> TuMenh
              Chi.Mui -> CauTran
            }
        6,
        12 ->
            when (day.chi) {
              Chi.Tuat -> ThanhLong
              Chi.Hoi -> MinhDuong
              Chi.Ty -> ThienHinh
              Chi.Suu -> ChuTuoc
              Chi.Dan -> KimQuy
              Chi.Mao -> KimDuong
              Chi.Thin -> BachHo
              Chi.Ty_ -> NgocDuong
              Chi.Ngo -> ThienLao
              Chi.Mui -> NguyenVu
              Chi.Than -> TuMenh
              Chi.Dau -> CauTran
            }
        else -> ThanhLong
      }
    }
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

private val AuspiciousHoursList: List<List<Boolean>> =
    listOf(
            "110100101100",
            "001101001011",
            "110011010010",
            "101100110100",
            "001011001101",
            "010010110011",
        )
        .map { row -> row.map { it == '1' } }

private fun calcAuspiciousHours(day: LunarDay): List<String> =
    AuspiciousHoursList[day.chi.ordinal % 6].withIndex()
        .filter { it.value }
        .map { (i, _) ->
          "${Chi.entries[i].displayName} (${(i * 2 + 23) % 24}-${(i * 2 + 1) % 24}h)"
        }

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

  val auspiciousHours
    get() = "Giờ Hoàng Đạo: ${calcAuspiciousHours(day).joinToString(", ")}"
}
