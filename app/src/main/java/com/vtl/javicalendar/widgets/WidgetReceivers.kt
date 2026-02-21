package com.vtl.javicalendar.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.vtl.javicalendar.worker.DailyUpdateWorker
import java.time.Duration

class CombinedWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = CombinedWidget()

  override fun onUpdate(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetIds: IntArray,
  ) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    Log.v("WidgetReceiver", "CombinedWidgetReceiver triggerUpdate onUpdate")
    DailyUpdateWorker.schedule(context, Duration.ZERO)
  }
}

class DayDetailsWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = DayDetailsWidget()

  override fun onUpdate(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetIds: IntArray,
  ) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    Log.v("WidgetReceiver", "DayDetailsWidgetReceiver triggerUpdate onUpdate")
    DailyUpdateWorker.schedule(context, Duration.ZERO)
  }
}

class MonthGridWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = MonthGridWidget()

  override fun onUpdate(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetIds: IntArray,
  ) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    Log.v("WidgetReceiver", "DayDetailsWidgetReceiver triggerUpdate onUpdate")
    DailyUpdateWorker.schedule(context, Duration.ZERO)
  }
}
