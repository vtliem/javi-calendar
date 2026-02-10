package com.vtl.javicalendar.data.datasource

import android.content.Context

data class HolidayData(val lastModified: Long, val content: String)

class HolidayLocalDataSource(context: Context) {
  companion object {
    private const val KEY_LAST_MODIFIED = "last_modified"
    private const val KEY_CONTENT = "content"
  }

  private val prefs = context.getSharedPreferences("holiday_local_prefs", Context.MODE_PRIVATE)

  fun saveData(data: HolidayData) {
    prefs.edit().apply {
      putLong(KEY_LAST_MODIFIED, data.lastModified)
      putString(KEY_CONTENT, data.content)
      apply()
    }
  }

  fun loadData(): HolidayData? {
    val content = prefs.getString(KEY_CONTENT, null) ?: return null
    val lastModified = prefs.getLong(KEY_LAST_MODIFIED, 0L)
    return HolidayData(lastModified, content)
  }
}
