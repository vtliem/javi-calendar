package com.vtl.javicalendar.widgets

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.vtl.javicalendar.presentation.model.CalendarSources
import kotlinx.serialization.json.Json

object WidgetManager {
  private val json = Json { ignoreUnknownKeys = true }

  suspend fun triggerUpdate(context: Context, sources: CalendarSources) {
    val manager = GlanceAppWidgetManager(context)
    val glanceIds = manager.getGlanceIds(CombinedWidget::class.java)
    if (glanceIds.isEmpty()) return

    // 2. Serialize
    val sources = json.encodeToString(sources)

    // 3. Push to Glance State
    glanceIds.forEach { id ->
      updateAppWidgetState(context, id) { prefs ->
        prefs[stringPreferencesKey("sources")] = sources
      }
      CombinedWidget().update(context, id)
    }
  }
}
