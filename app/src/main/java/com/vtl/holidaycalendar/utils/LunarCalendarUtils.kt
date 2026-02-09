package com.vtl.holidaycalendar.utils

import kotlin.math.*

/**
 * Vietnamese Lunar Calendar implementation based on Ho Ngoc Duc's algorithm.
 */
object LunarCalendarUtils {

    private val CAN = listOf("Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý")
    private val CHI = listOf("Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi")
    private val GIO_HD = listOf(
        "110100101100", "001101001011", "110011010010", "101100110100", "001011001101", "010010110011"
    )
    private val LUC_DIEU = listOf("Đại An", "Lưu Liên", "Tốc Hỷ", "Xích Khẩu", "Tiểu Cát", "Không Vong")
    private val HOANG_DAO = listOf( "Đại An", "Tốc Hỷ" , "Tiểu Cát")

    private val LUNAR_OBSERVANCES = mapOf(
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
        "23/12" to "Tiễn Táo Quân về trời"
    )

    private fun INT(d: Double): Int = floor(d).toInt()

    private fun date2JuliusDay(dd: Int, mm: Int, yy: Int): Int {
        var d = dd
        var m = mm
        var y = yy
        val a = (14 - m) / 12
        val yf = y.toDouble() + 4800.0 - a.toDouble()
        val mf = m.toDouble() + 12.0 * a.toDouble() - 3.0
        var jd = d.toDouble() + floor((153.0 * mf + 2.0) / 5.0) + 365.0 * yf + floor(yf / 4.0) - floor(yf / 100.0) + floor(yf / 400.0) - 32045.0
        if (jd < 2299161.0) {
            jd = d.toDouble() + floor((153.0 * mf + 2.0) / 5.0) + 365.0 * yf + floor(yf / 4.0) - 32083.0
        }
        return jd.toInt()
    }

    private fun juliusDay2Date(jd: Int): IntArray {
        var a: Int
        var b: Int
        var c: Int
        if (jd > 2299160) {
            a = jd + 32044
            b = (4 * a + 3) / 146097
            c = a - (b * 146097) / 4
        } else {
            b = 0
            c = jd + 32082
        }
        val d = (4 * c + 3) / 1461
        val e = c - (1461 * d) / 4
        val m = (5 * e + 2) / 153
        val day = e - (153 * m + 2) / 5 + 1
        val month = m + 3 - 12 * (m / 10)
        val year = b * 100 + d - 4800 + (m / 10)
        return intArrayOf(day, month, year)
    }

    private fun newMoon(k: Int): Double {
        val kf = k.toDouble()
        val t = kf / 1236.85
        val t2 = t * t
        val t3 = t2 * t
        val dr = PI / 180.0
        var jd1 = 2415020.75933 + 29.53058868 * kf + 0.0001178 * t2 - 0.000000155 * t3
        jd1 += 0.00033 * sin((166.56 + 132.87 * t - 0.009173 * t2) * dr)
        val m = (359.2242 + 29.10535608 * kf - 0.0000333 * t2 - 0.00000347 * t3) * dr
        val mpr = (306.0253 + 385.81691806 * kf + 0.0107306 * t2 + 0.00001236 * t3) * dr
        val f = (21.2964 + 390.67050646 * kf - 0.0016528 * t2 - 0.00000239 * t3) * dr
        var c1 = (0.1734 - 0.000393 * t) * sin(m) + 0.0021 * sin(2.0 * m)
        c1 -= 0.4068 * sin(mpr) + 0.0161 * sin(2.0 * mpr)
        c1 -= 0.0004 * sin(3.0 * mpr)
        c1 += 0.0104 * sin(2.0 * f) - 0.0051 * sin(m + mpr)
        c1 -= 0.0074 * sin(m - mpr) + 0.0004 * sin(2.0 * f + m)
        c1 -= 0.0004 * sin(2.0 * f - m) - 0.0006 * sin(2.0 * f + mpr)
        c1 += 0.0100 * sin(2.0 * f - mpr) + 0.0005 * sin(2.0 * mpr + m)
        
        val deltat = if (t < -11.0) {
            0.001 + 0.000839 * t + 0.0002261 * t2 - 0.00000845 * t3 - 0.000000081 * t * t3
        } else {
            -0.000278 + 0.000265 * t + 0.000262 * t2
        }
        return jd1 + c1 - deltat
    }

    private fun getNewMoonDay(k: Int, tz: Double): Int {
        return floor(newMoon(k) + 0.5 + tz / 24.0).toInt()
    }

    private fun sunLongitude(jdn: Double): Double {
        val t = (jdn - 2451545.0) / 36525.0
        val t2 = t * t
        val dr = PI / 180.0
        val m = (357.52910 + 35999.05030 * t - 0.0001559 * t2 - 0.00000048 * t * t2) * dr
        val l0 = 280.46645 + 36000.76983 * t + 0.0003032 * t2
        val dl = (1.914600 - 0.004817 * t - 0.000014 * t2) * sin(m) +
                (0.019993 - 0.000101 * t) * sin(2.0 * m) + 0.00029 * sin(3.0 * m)
        var l = (l0 + dl) * dr
        l -= 2.0 * PI * floor(l / (2.0 * PI))
        return l
    }

    private fun getSunLongitude(d: Int, tz: Double): Int {
        return floor((sunLongitude(d.toDouble() - 0.5 - tz / 24.0) / PI) * 6.0).toInt()
    }

    private fun getLunarMonth11(yy: Int, tz: Double): Int {
        val off = date2JuliusDay(31, 12, yy) - 2415021
        val k = floor(off.toDouble() / 29.530588853).toInt()
        var nm = getNewMoonDay(k, tz)
        val sunLong = getSunLongitude(nm, tz)
        if (sunLong >= 9) {
            nm = getNewMoonDay(k - 1, tz)
        }
        return nm
    }

    private fun getLeapMonthOffset(a11: Double, tz: Double): Int {
        val k = floor((a11 - 2415021.076998695) / 29.530588853 + 0.5).toInt()
        var i = 1
        var arc = getSunLongitude(getNewMoonDay(k + i, tz), tz)
        while (true) {
            val last = arc
            i++
            val newmoon = getNewMoonDay(k + i, tz)
            arc = getSunLongitude(newmoon, tz)
            if (arc == last || i >= 14) break
        }
        return i - 1
    }

    fun convertSolarToLunar(dd: Int, mm: Int, yy: Int, timeZone: Double = 7.0): com.vtl.holidaycalendar.domain.model.LunarDate {
        val dayNumber = date2JuliusDay(dd, mm, yy)
        val k = floor((dayNumber.toDouble() - 2415021.076998695) / 29.530588853).toInt()
        var monthStart = getNewMoonDay(k + 1, timeZone)
        if (monthStart > dayNumber) {
            monthStart = getNewMoonDay(k, timeZone)
        }
        
        var a11 = getLunarMonth11(yy, timeZone)
        var b11 = a11
        var lunarYear: Int
        if (a11 >= monthStart) {
            lunarYear = yy
            a11 = getLunarMonth11(yy - 1, timeZone)
        } else {
            lunarYear = yy + 1
            b11 = getLunarMonth11(yy + 1, timeZone)
        }
        
        val lunarDay = dayNumber - monthStart + 1
        val diff = floor((monthStart.toDouble() - a11.toDouble()) / 29.0).toInt()
        
        var lunarLeap = 0
        var lunarMonth = diff + 11
        if (b11 - a11 > 365) {
            val leapMonthDiff = getLeapMonthOffset(a11.toDouble(), timeZone)
            if (diff >= leapMonthDiff) {
                lunarMonth = diff + 10
                if (diff == leapMonthDiff) lunarLeap = 1
            }
        }
        if (lunarMonth > 12) lunarMonth -= 12
        if (lunarMonth >= 11 && diff < 4) lunarYear -= 1

        val dayName = LUC_DIEU[(dayNumber + lunarMonth - 1) % 6]
        val isAuspicious = HOANG_DAO.contains(dayName)
        
        val chiIndex = (dayNumber + 1) % 12
        val auspiciousHours = calculateGioHoangDao(chiIndex)
        val canChi = getCanChiDay(dayNumber)
        val monthCanChi = getCanChiMonth(lunarMonth, lunarYear)
        val yearCanChi = getCanChiYear(lunarYear)

        return com.vtl.holidaycalendar.domain.model.LunarDate(
            day = lunarDay,
            month = lunarMonth,
            year = lunarYear,
            isLeap = lunarLeap == 1,
            canChi = canChi,
            monthCanChi = monthCanChi,
            yearCanChi = yearCanChi,
            observance = LUNAR_OBSERVANCES["$lunarDay/$lunarMonth"],
            isAuspicious = isAuspicious,
            statusLabel = dayName,
            statusPrefix = if (isAuspicious) "Ngày hoàng đạo" else "Ngày hắc đạo",
            auspiciousHours = auspiciousHours
        )
    }

    private fun calculateGioHoangDao(chiOfDateIndex: Int): String {
        val gioHD = GIO_HD[chiOfDateIndex % 6]
        val sb = StringBuilder("Giờ Hoàng Đạo: ")
        var count = 0
        for (i in 0 until 12) {
            if (gioHD[i] == '1') {
                sb.append(CHI[i])
                sb.append(" (${(i * 2 + 23) % 24}-${(i * 2 + 1) % 24})")
                if (count++ < 5) sb.append(", ")
            }
        }
        return sb.toString()
    }

    private fun getCanChiDay(jd: Int): String {
        val can = CAN[(jd + 9) % 10]
        val chi = CHI[(jd + 1) % 12]
        return "$can $chi"
    }

    private fun getCanChiMonth(month: Int, year: Int): String {
        val canYearIndex = (year + 6) % 10
        val startCanM1 = (canYearIndex * 2 + 2) % 10
        val canInx = (startCanM1 + (month - 1)) % 10
        val chiInx = (month + 1) % 12
        return "${CAN[canInx]} ${CHI[chiInx]}"
    }

    private fun getCanChiYear(year: Int): String {
        val can = CAN[(year + 6) % 10]
        val chi = CHI[(year + 8) % 12]
        return "$can $chi"
    }

    fun lunarMonthName(lunarMonth: Int) =  when(lunarMonth) {
        1 -> "Giêng"
        11 -> "Một"
        12 -> "Chạp"
        else -> lunarMonth.toString()
    }
}
