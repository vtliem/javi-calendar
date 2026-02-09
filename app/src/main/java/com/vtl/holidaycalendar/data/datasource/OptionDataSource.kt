package com.vtl.holidaycalendar.data.datasource

import android.content.Context
import android.content.SharedPreferences
import com.vtl.holidaycalendar.presentation.model.Option

class OptionDataSource(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("calendar_options", Context.MODE_PRIVATE)

    fun saveOption(option: Option) {
        prefs.edit().apply {
            putBoolean("japaneseInfo", option.japaneseInfo)
            putBoolean("lucDieu", option.lucDieu)
            putBoolean("observance", option.observance)
            putBoolean("monthLucDieu", option.monthLucDieu)
            putBoolean("monthJapaneseHoliday", option.monthJapaneseHoliday)
            putBoolean("monthObservance", option.monthObservance)
            apply()
        }
    }

    fun loadOption(): Option {
        return Option(
            japaneseInfo = prefs.getBoolean("japaneseInfo", true),
            lucDieu = prefs.getBoolean("lucDieu", true),
            observance = prefs.getBoolean("observance", true),
            monthLucDieu = prefs.getBoolean("monthLucDieu", true),
            monthJapaneseHoliday = prefs.getBoolean("monthJapaneseHoliday", true),
            monthObservance = prefs.getBoolean("monthObservance", true)
        )
    }
}
