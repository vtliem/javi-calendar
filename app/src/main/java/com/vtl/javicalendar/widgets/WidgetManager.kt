package com.vtl.javicalendar.widgets

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.vtl.javicalendar.presentation.model.CalendarSources
import kotlinx.serialization.json.Json

object WidgetManager {
  private val json = Json { ignoreUnknownKeys = true }
  private val key = stringPreferencesKey("sources")

  private suspend fun getGlanceIds(context: Context) =
      GlanceAppWidgetManager(context).getGlanceIds(CombinedWidget::class.java)

  suspend fun triggerUpdate(context: Context, sources: CalendarSources) {
    triggerUpdate(context, getGlanceIds(context), sources)
  }

  private suspend fun triggerUpdate(
      context: Context,
      glanceIds: List<GlanceId>,
      sources: CalendarSources,
  ) {
    if (glanceIds.isEmpty()) return
    val sources = json.encodeToString(sources)
    glanceIds.forEach { id ->
      updateAppWidgetState(context, id) { prefs -> prefs[key] = sources }
      CombinedWidget().update(context, id)
    }
  }

  fun loadSources(prefs: Preferences): CalendarSources? {
    val sourcesJson = prefs[key]
    return sourcesJson
        ?.let { runCatching { json.decodeFromString<CalendarSources>(it) }.getOrNull() }
        ?.let {
          return it
        }
  }
}
