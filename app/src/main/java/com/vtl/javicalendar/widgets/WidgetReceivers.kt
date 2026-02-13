package com.vtl.javicalendar.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.vtl.javicalendar.worker.DailyUpdateWorker
import java.time.Duration

class CombinedWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = CombinedWidget()

  @OptIn(ExperimentalGlanceApi::class)
  override fun onUpdate(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetIds: IntArray,
  ) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    Log.v("CombinedWidgetReceiver", "triggerUpdate onUpdate")
    DailyUpdateWorker.schedule(context, Duration.ZERO)
  }
}
