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
  ThanhLong("Thanh Long", true, "Mọi việc đều thuận lợi, trăm sự thành công."),
  MinhDuong("Minh Đường", true, "Tốt cho việc khởi đầu, cầu quan tiến chức."),
  ThienHinh("Thiên Hình", false, "Dễ gặp rắc rối pháp lý, kiện tụng."),
  ChuTuoc("Chu Tước", false, "Đề phòng thị phi, cãi vã, tai tiếng."),
  KimQuy("Kim Quỹ", true, "Rất tốt cho kinh doanh, cầu tài lộc."),
  KimDuong("Bảo Quang", true, "Tốt cho xây dựng, nhà cửa, đi xa."),
  BachHo("Bạch Hổ", false, "Kỵ đi xa, đề phòng tai nạn bất ngờ."),
  NgocDuong("Ngọc Đường", true, "Được quý nhân giúp đỡ, vạn sự hanh thông."),
  ThienLao("Thiên Lao", false, "Dễ bị đình trệ, kìm hãm, khó tiến triển."),
  NguyenVu("Huyền Vũ", false, "Hao tốn tiền bạc, dễ bị mất trộm."),
  TuMenh("Tư Mệnh", true, "Sức khỏe dồi dào, cát lành cho mọi việc."),
  CauTran("Câu Trận", false, "Đề phòng tiểu nhân quấy phá, tranh chấp.");

  val typeName
    get() = if (isAuspicious) "Hoàng Đạo" else "Hắc Đạo"

  override fun toString() = "$zodiacName $typeName"

  companion object {
    fun of(
        lunarMonth: Int,
        julianDay: Int,
    ): Zodiac {
      val startChiOfMonth =
          when (lunarMonth % 6) {
            1 -> 2 // Tháng 1 & 7: Thanh Long tại Dần
            2 -> 4 // Tháng 2 & 8: Thanh Long tại Thìn
            3 -> 6 // Tháng 3 & 9: Thanh Long tại Ngọ
            4 -> 8 // Tháng 4 & 10: Thanh Long tại Thân
            5 -> 10 // Tháng 5 & 11: Thanh Long tại Tuất
            0 -> 0 // Tháng 6 & 12: Thanh Long tại Tý
            else -> 0
          }

      val godIndex = (Chi.ofDay(julianDay).ordinal - startChiOfMonth + 12) % 12
      return entries[godIndex]
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

private fun calcAuspiciousHours(julianDay: Int): List<String> =
    AuspiciousHoursList[Chi.ofDay(julianDay).ordinal % 6].withIndex()
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
  val observance by lazy { LunarObservances["$lunarDay/$lunarMonth"] }
  val zodiac by lazy { Zodiac.of(lunarMonth, julianDay) }
  val auspiciousHours by lazy {
    "Giờ Hoàng Đạo: ${calcAuspiciousHours(julianDay).joinToString(", ")}"
  }
}
