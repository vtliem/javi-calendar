package com.vtl.javicalendar.widgets

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.vtl.javicalendar.MainActivity
import com.vtl.javicalendar.domain.CalendarFactory
import com.vtl.javicalendar.presentation.model.CalendarSources
import com.vtl.javicalendar.presentation.model.DateInfo
import com.vtl.javicalendar.presentation.model.MonthInfo
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.presentation.theme.*
import com.vtl.javicalendar.widgets.WidgetManager.loadOrCreateSources
import com.vtl.javicalendar.widgets.components.WidgetDayDetails
import com.vtl.javicalendar.widgets.components.WidgetMonthGrid

class CombinedWidget : GlanceAppWidget() {
  private data class WidgetData(
      val monthInfo: MonthInfo,
      val dateInfo: DateInfo?,
      val option: Option,
  )

  override val sizeMode = SizeMode.Exact

  override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

  private fun createData(sources: CalendarSources): WidgetData {
    val today = sources.today
    val monthInfo =
        CalendarFactory.createMonthInfo(today.year, today.monthValue, sources.holidays, today)

    val dateInfo = monthInfo.getDate(today)
    return WidgetData(monthInfo, dateInfo, sources.option)
  }

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val prefs = currentState<Preferences>()
      val widgetSize = LocalSize.current

      val state by
          produceState<WidgetData?>(initialValue = null, prefs) {
            val sources = loadOrCreateSources(context, prefs)
            value = createData(sources)
          }
      val displayOption = state?.option?.adjustBySize(widgetSize) ?: Option()
      Log.v("CombinedWidget", "$widgetSize $displayOption")
      CombinedWidgetContent(state?.monthInfo, state?.dateInfo, displayOption)
    }
  }

  @Composable
  private fun CombinedWidgetContent(monthInfo: MonthInfo?, dateInfo: DateInfo?, option: Option) {
    Column(
        modifier =
            GlanceModifier.fillMaxWidth()
                .wrapContentHeight()
                .background(
                    ColorProvider(day = WidgetBackgroundLight, night = WidgetBackgroundDark)
                )
                .padding(8.dp)
                .clickable(actionStartActivity<MainActivity>())
    ) {
      WidgetDayDetails(dateInfo, option)

      Spacer(modifier = GlanceModifier.height(8.dp))

      Spacer(modifier = GlanceModifier.height(8.dp))

      WidgetMonthGrid(monthInfo, option)
    }
  }
}
