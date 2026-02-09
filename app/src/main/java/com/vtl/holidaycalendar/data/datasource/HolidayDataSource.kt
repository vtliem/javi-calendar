package com.vtl.holidaycalendar.data.datasource

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

class HolidayDataSource(val context: Context) {
    private val CSV_URL = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv"
    private val PREFS_NAME = "holiday_prefs"
    private val KEY_LAST_FETCH = "last_fetch_time"
    private val KEY_HOLIDAY_DATA = "holiday_data"

    suspend fun fetchHolidays(): String? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(CSV_URL).openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()
            // The official CSV is Shift-JIS encoded
            val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("Shift-JIS")))
            val content = reader.use { it.readText() }
            content
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveToCache(data: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_HOLIDAY_DATA, data)
            .putLong(KEY_LAST_FETCH, System.currentTimeMillis())
            .apply()
    }

    fun getFromCache(): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_HOLIDAY_DATA, null)
    }

    fun shouldRefresh(): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastFetch = prefs.getLong(KEY_LAST_FETCH, 0)
        val oneDayMillis = 24 * 60 * 60 * 1000L
        return System.currentTimeMillis() - lastFetch > oneDayMillis
    }
}
