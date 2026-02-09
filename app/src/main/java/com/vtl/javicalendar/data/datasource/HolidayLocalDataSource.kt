package com.vtl.javicalendar.data.datasource

import android.content.Context

data class HolidayData(val etag: String?, val content: String)

class HolidayLocalDataSource(context: Context) {
    companion object{
        private const val KEY_ETAG = "etag"
        private const val KEY_CONTENT = "content"
    }
    private val prefs = context.getSharedPreferences("holiday_local_prefs", Context.MODE_PRIVATE)


    fun saveData(data: HolidayData) {
        prefs.edit().apply {
            putString(KEY_ETAG, data.etag)
            putString(KEY_CONTENT, data.content)
            apply()
        }
    }

    fun loadData(): HolidayData? {
        val content = prefs.getString(KEY_CONTENT, null) ?: return null
        val etag = prefs.getString(KEY_ETAG, null)
        return HolidayData(etag, content)
    }
}
