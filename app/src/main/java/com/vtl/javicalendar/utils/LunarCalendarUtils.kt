package com.vtl.javicalendar.utils

import com.vtl.javicalendar.domain.model.LunarDate
import kotlin.math.*

/** Vietnamese Lunar Calendar implementation based on Ho Ngoc Duc's algorithm. */
object LunarCalendarUtils {

  private fun date2julianDay(d: Int, m: Int, y: Int): Int {
    val a = (14 - m) / 12
    val yf = y.toDouble() + 4800.0 - a.toDouble()
    val mf = m.toDouble() + 12.0 * a.toDouble() - 3.0
    var jd =
        d.toDouble() + floor((153.0 * mf + 2.0) / 5.0) + 365.0 * yf + floor(yf / 4.0) -
            floor(yf / 100.0) + floor(yf / 400.0) - 32045.0
    if (jd < 2299161.0) {
      jd = d.toDouble() + floor((153.0 * mf + 2.0) / 5.0) + 365.0 * yf + floor(yf / 4.0) - 32083.0
    }
    return jd.toInt()
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

    val deltat =
        if (t < -11.0) {
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
    val dl =
        (1.914600 - 0.004817 * t - 0.000014 * t2) * sin(m) +
            (0.019993 - 0.000101 * t) * sin(2.0 * m) +
            0.00029 * sin(3.0 * m)
    var l = (l0 + dl) * dr
    l -= 2.0 * PI * floor(l / (2.0 * PI))
    return l
  }

  private fun getSunLongitude(d: Int, tz: Double): Int {
    return floor((sunLongitude(d.toDouble() - 0.5 - tz / 24.0) / PI) * 6.0).toInt()
  }

  private fun getLunarMonth11(yy: Int, tz: Double): Int {
    val off = date2julianDay(31, 12, yy) - 2415021
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

  fun convertSolarToLunar(
      dd: Int,
      mm: Int,
      yy: Int,
      timeZone: Double = 7.0,
  ): LunarDate {
    val julianDay = date2julianDay(dd, mm, yy)
    val k = floor((julianDay.toDouble() - 2415021.076998695) / 29.530588853).toInt()

    var monthStart = getNewMoonDay(k + 1, timeZone)
    if (monthStart > julianDay) {
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

    val lunarDay = julianDay - monthStart + 1
    val diff = floor((monthStart.toDouble() - a11.toDouble()) / 29.0).toInt()

    var lunarMonth = diff + 11
    var isCurrentMonthLeap = false
    var leapMonthInYear: Int? = null
    if (b11 - a11 > 365) {
      val leapMonthOffset = getLeapMonthOffset(a11.toDouble(), timeZone)
      leapMonthInYear = (leapMonthOffset + 10).let { if (it > 12) it - 12 else it }
      if (diff >= leapMonthOffset) {
        lunarMonth = diff + 10
        if (diff == leapMonthOffset) isCurrentMonthLeap = true
      }
    }
    if (lunarMonth > 12) lunarMonth -= 12
    if (lunarMonth >= 11 && diff < 4) {
      lunarYear -= 1
      // If the next year was leap, this specific month (belonging to the previous year)
      // should not carry the leapMonth status of the next year.
      leapMonthInYear = null
    }

    return LunarDate(
        lunarYear = lunarYear,
        lunarMonth = lunarMonth,
        lunarDay = lunarDay,
        leapMonth = leapMonthInYear,
        isLeapMonth = isCurrentMonthLeap,
        julianDay = julianDay,
    )
  }
}
