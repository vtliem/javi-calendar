package com.vtl.javicalendar.utils

import kotlin.math.floor

object JulianDayCalculator {
  fun date2julianDay(d: Int, m: Int, y: Int): Int {
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
}
