package com.vtl.javicalendar.widgets

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.vtl.javicalendar.domain.CalendarFactory
import com.vtl.javicalendar.presentation.model.CalendarSources
import com.vtl.javicalendar.presentation.model.DateInfo
import com.vtl.javicalendar.presentation.model.MonthInfo
import com.vtl.javicalendar.presentation.model.Option
import kotlinx.serialization.json.Json

data class WidgetData(
    val monthInfo: MonthInfo,
    val dateInfo: DateInfo?,
    val option: Option,
)

object WidgetManager {
  private val json = Json { ignoreUnknownKeys = true }
  private val key = stringPreferencesKey("sources")

  fun createData(sources: CalendarSources): WidgetData {
    val today = sources.today
    val monthInfo =
        CalendarFactory.createMonthInfo(
            sources.option.sundayFirst,
            today.year,
            today.monthValue,
            sources.holidays,
            today,
        )

    val dateInfo = monthInfo.getDate(today)
    return WidgetData(monthInfo, dateInfo, sources.option)
  }

  suspend fun triggerUpdate(context: Context, sources: CalendarSources) {
    val manager = GlanceAppWidgetManager(context)
    val combinedIds = manager.getGlanceIds(CombinedWidget::class.java)
    val dayDetailsIds = manager.getGlanceIds(DayDetailsWidget::class.java)
    val monthGridIds = manager.getGlanceIds(MonthGridWidget::class.java)

    val sourcesJson = json.encodeToString(sources)

    updateWidgets(context, combinedIds, sourcesJson, CombinedWidget())
    updateWidgets(context, dayDetailsIds, sourcesJson, DayDetailsWidget())
    updateWidgets(context, monthGridIds, sourcesJson, MonthGridWidget())
  }

  private suspend fun updateWidgets(
      context: Context,
      glanceIds: List<GlanceId>,
      sourcesJson: String,
      widget: GlanceAppWidget,
  ) {
    glanceIds.forEach { id ->
      updateAppWidgetState(context, id) { prefs -> prefs[key] = sourcesJson }
      widget.update(context, id)
    }
  }

  fun loadSources(prefs: Preferences): CalendarSources? {
    val sourcesJson = prefs[key]
    return sourcesJson?.let {
      runCatching { json.decodeFromString<CalendarSources>(it) }.getOrNull()
    }
  }
}
