package com.vtl.javicalendar.widgets

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.vtl.javicalendar.data.datasource.HolidayLocalDataSource
import com.vtl.javicalendar.data.datasource.OptionDataSource
import com.vtl.javicalendar.domain.CalendarFactory
import com.vtl.javicalendar.domain.model.JapaneseHolidays
import kotlinx.serialization.json.Json
import java.time.LocalDate

object WidgetManager {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun triggerUpdate(context: Context) {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(CombinedWidget::class.java)
        if (glanceIds.isEmpty()) return

        // 1. Calculate data
        val today = LocalDate.now()
        val holidayLocalDataSource = HolidayLocalDataSource(context)
        val holidays = JapaneseHolidays.parseHolidays(holidayLocalDataSource.loadData()?.content ?: "")
        val option = OptionDataSource(context).loadOption()
        val monthInfo = CalendarFactory.createMonthInfo(today.year, today.monthValue, holidays, today)

        // 2. Serialize
        val monthJson = json.encodeToString(monthInfo)
        val optionJson = json.encodeToString(option)

        // 3. Push to Glance State
        glanceIds.forEach { id ->
            updateAppWidgetState(context, id) { prefs ->
                prefs[stringPreferencesKey("month_data")] = monthJson
                prefs[stringPreferencesKey("option_data")] = optionJson
                prefs[longPreferencesKey("last_update")] = System.currentTimeMillis()
            }
            CombinedWidget().update(context, id)
        }
    }
}
