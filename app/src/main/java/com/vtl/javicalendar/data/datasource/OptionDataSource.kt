package com.vtl.javicalendar.data.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.vtl.javicalendar.presentation.model.Option
import kotlinx.serialization.json.Json

class OptionDataSource(context: Context) {
  private val prefs: SharedPreferences =
      context.getSharedPreferences("calendar_options", Context.MODE_PRIVATE)

  private val json = Json { ignoreUnknownKeys = true }

  fun saveOption(option: Option) {
    val jsonString = json.encodeToString(option)
    prefs.edit { putString(KEY_OPTION, jsonString) }
  }

  fun loadOption(): Option {
    val jsonString = prefs.getString(KEY_OPTION, null) ?: return Option()
    return runCatching { json.decodeFromString<Option>(jsonString) }.getOrDefault(Option())
  }

  companion object {
    private const val KEY_OPTION = "option_json"
  }
}
