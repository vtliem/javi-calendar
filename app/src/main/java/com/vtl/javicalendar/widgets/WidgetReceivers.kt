package com.vtl.javicalendar.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.vtl.javicalendar.worker.DailyUpdateWorker
import java.time.Duration

abstract class BaseWidgetReceiver : GlanceAppWidgetReceiver() {
  override fun onUpdate(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetIds: IntArray,
  ) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    Log.v("WidgetReceiver", "${this.javaClass.simpleName} onUpdate")
    DailyUpdateWorker.schedule(context, Duration.ZERO)
  }

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    when (intent.action) {
      Intent.ACTION_DATE_CHANGED,
      Intent.ACTION_TIME_CHANGED,
      Intent.ACTION_TIMEZONE_CHANGED -> {
        Log.v(
            "WidgetReceiver",
            "${this.javaClass.simpleName} Action received: ${intent.action}. Triggering update.",
        )
        DailyUpdateWorker.schedule(context, Duration.ZERO)
      }
    }
  }
}

class CombinedWidgetReceiver : BaseWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = CombinedWidget()
}

class DayDetailsWidgetReceiver : BaseWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = DayDetailsWidget()
}

class MonthGridWidgetReceiver : BaseWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = MonthGridWidget()
}
