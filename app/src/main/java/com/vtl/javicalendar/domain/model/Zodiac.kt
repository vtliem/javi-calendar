package com.vtl.javicalendar.domain.model

enum class Zodiac(
    val zodiacName: String,
    val isAuspicious: Boolean,
    val detail: String,
) {
  ThanhLong("Thanh Long", true, "✨ Tốt cho cưới hỏi, khai trương, thi cử và các việc hỷ sự."),
  MinhDuong(
      "Minh Đường",
      true,
      "✨ Tốt cho nhập học, khai trương, nhậm chức, giao thương, động thổ.",
  ),
  ThienHinh(
      "Thiên Hình",
      false,
      "☁️ Rất xấu cho cưới hỏi, xây dựng, nhậm chức, nhập học, khai trương, mua xe.",
  ),
  ChuTuoc(
      "Chu Tước",
      false,
      "☁️ Rất xấu cho khai trương, mở xưởng, nhập trạch và các việc cầu tài lộc.",
  ),
  KimQuy("Kim Quỹ", true, "✨ Lý tưởng cho hôn sự, giao tiếp, thỏa thuận, hội họp và tranh biện."),
  KimDuong("Bảo Quang", true, "✨ Tốt cho khởi công, động thổ, khai trương, cưới hỏi, nhậm chức."),
  BachHo(
      "Bạch Hổ",
      false,
      "☁️ Rất xấu cho mọi việc, tối kỵ mai táng và các việc liên quan đến đất đai.",
  ),
  NgocDuong(
      "Ngọc Đường",
      true,
      "✨ Rất tốt cho thi cử, khai trương, động thổ, nhậm chức và phát triển tài năng.",
  ),
  ThienLao("Thiên Lao", false, "☁️ Xấu cho động thổ, nhập trạch, xuất hành, chữa bệnh, cưới hỏi."),
  NguyenVu("Huyền Vũ", false, "☁️ Rất xấu cho cưới hỏi, làm nhà, nhập trạch, ký kết, khai trương."),
  TuMenh("Tư Mệnh", true, "✨ Rất tốt cho khai trương, động thổ, ký kết hợp đồng, cưới hỏi."),
  CauTran(
      "Câu Trận",
      false,
      "☁️ Rất xấu cho cưới hỏi, động thổ, đổ mái, xuất hành, tu tạo, tế tự.",
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

enum class Duty(
    val dutyName: String,
    val goodFor: String,
    val badFor: String,
) {
  Kien(
      "Kiến",
      "👍 Tốt cho khởi đầu mới, khai trương, nhậm chức, cưới hỏi, trồng cây.",
      "👎 Nên tránh động thổ, chôn cất, đào giếng, lợp nhà.",
  ),
  Tru(
      "Trừ",
      "👍 Tốt cho việc cúng bái, giải hạn, tẩy trần, chữa bệnh, vệ sinh nhà cửa.",
      "👎 Nên tránh ký kết hợp đồng, khai trương, cưới hỏi, chi tiêu khoản tiền lớn.",
  ),
  Man(
      "Mãn",
      "👍 Tốt cho việc cầu tài lộc, cầu phúc, cúng lễ, xuất hành, sửa sang kho tàng.",
      "👎 Nên tránh việc chôn cất, kiện tụng hoặc nhận nhiệm vụ mới.",
  ),
  Binh("Bình", "👍 Mọi việc đều tốt, đặc biệt là giao thương, mua bán, sửa chữa bếp.", ""),
  Dinh(
      "Định",
      "👍 Tốt cho việc ký kết hợp đồng, mua bán, làm chuồng trại gia súc.",
      "👎 Nên tránh các việc thưa kiện hoặc xuất hành đi xa.",
  ),
  Chap(
      "Chấp",
      "👍 Tốt cho tu sửa nhà cửa, xây dựng, tuyển dụng, thuê mướn nhân sự.",
      "👎 Nên tránh xuất nhập kho, chi tiền hoặc kê giường ngủ (an sàng).",
  ),
  Pha(
      "Phá",
      "👍 Tốt cho việc dỡ bỏ nhà cũ, phá bỏ các thứ lỗi thời, đi xa.",
      "👎 Nên tránh mở hàng kinh doanh, cưới hỏi, hội họp quan trọng.",
  ),
  Nguy(
      "Nguy",
      "👍 Tốt cho việc lễ bái, cầu tự, tụng kinh, làm việc thiện.",
      "👎 Nên tránh khai trương, động thổ, cưới hỏi, đi xa hoặc leo cao.",
  ),
  Thanh(
      "Thành",
      "👍 Rất tốt cho nhập học, kết hôn, khai trương, dọn về nhà mới.",
      "👎 Nên tránh việc kiện tụng, tranh chấp hoặc cãi vã.",
  ),
  Thau(
      "Thâu",
      "👍 Tốt cho mở cửa hàng, thu mua hàng hóa, thu nợ, tích lũy tài sản.",
      "👎 Nên tránh các việc liên quan đến ma chay, an táng, tảo mộ.",
  ),
  Khai(
      "Khai",
      "👍 Rất tốt cho động thổ làm nhà, kết hôn, khai trương, bắt đầu dự án mới.",
      "👎 Nên tránh việc an táng hoặc các việc liên quan đến tang lễ.",
  ),
  Be(
      "Bế",
      "👍 Tốt cho việc xây vá tường vách, đắp đê điều, xây cửa kho.",
      "👎 Nên tránh nhậm chức, khiếu kiện hoặc đào giếng.",
  );

  companion object {
    fun of(
        month: LunarMonth,
        day: LunarDay,
    ): Duty {
      val dutyIndex = (day.chi.ordinal - month.chi.ordinal + 12) % 12
      return entries[dutyIndex]
    }
  }
}

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

internal fun calcAuspiciousHours(day: LunarDay): List<String> =
    AuspiciousHoursList[day.chi.ordinal % 6].withIndex()
        .filter { it.value }
        .map { (i, _) ->
          "${Chi.entries[i].displayName} (${(i * 2 + 23) % 24}-${(i * 2 + 1) % 24}h)"
        }
