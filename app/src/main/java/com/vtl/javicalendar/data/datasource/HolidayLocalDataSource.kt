package com.vtl.javicalendar.data.datasource

import android.content.Context
import androidx.core.content.edit

data class HolidayData(
    val lastModified: Long,
    val content: String,
    val lastSuccess: Long = 0,
    val error: HolidayErrorType? = null,
)

class HolidayLocalDataSource(context: Context) {
  companion object {
    private const val KEY_LAST_MODIFIED = "last_modified"
    private const val KEY_CONTENT = "content"
    private const val KEY_ERROR = "error"
    private const val KEY_LAST_SUCCESS = "last_success"
  }

  private val prefs = context.getSharedPreferences("holiday_local_prefs", Context.MODE_PRIVATE)

  fun saveData(data: HolidayData) {
    prefs.edit {
      putLong(KEY_LAST_MODIFIED, data.lastModified)
      putString(KEY_CONTENT, data.content)
      putLong(KEY_LAST_SUCCESS, System.currentTimeMillis())
      remove(KEY_ERROR)
    }
  }

  fun saveError(error: HolidayErrorType, removeOld: Boolean) {
    prefs.edit {
      if (removeOld) {
        clear()
      }
      putString(KEY_ERROR, error.name)
    }
  }

  fun saveSuccess() {
    prefs.edit {
      remove(KEY_ERROR)
      putLong(KEY_LAST_SUCCESS, System.currentTimeMillis())
    }
  }

  fun loadLastModified(): Long {
    return prefs.getLong(KEY_LAST_MODIFIED, 0L)
  }

  fun loadData(): HolidayData? {
    val content = prefs.getString(KEY_CONTENT, null)?.takeIf { it.isNotEmpty() } ?: return null
    val lastModified = prefs.getLong(KEY_LAST_MODIFIED, 0L)
    val lastSuccess = prefs.getLong(KEY_LAST_SUCCESS, 0L)
    val error = prefs.getString(KEY_ERROR, null)?.let { HolidayErrorType.valueOf(it) }
    return HolidayData(lastModified, content, lastSuccess, error)
  }
}
