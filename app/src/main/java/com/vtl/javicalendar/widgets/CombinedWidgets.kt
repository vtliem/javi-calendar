package com.vtl.javicalendar.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.vtl.javicalendar.MainActivity
import com.vtl.javicalendar.presentation.model.DateInfo
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.color
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.displayName
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.presentation.theme.*
import com.vtl.javicalendar.utils.limitScaleSp
import com.vtl.javicalendar.widgets.WidgetManager.loadSources
import com.vtl.javicalendar.widgets.components.WidgetDayDetails
import com.vtl.javicalendar.widgets.components.WidgetMonthGrid
import com.vtl.javicalendar.widgets.components.widgetColor

abstract class BaseWidget : GlanceAppWidget() {
  override val sizeMode = SizeMode.Exact
  override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

  protected open fun displayOption(it: Option, widgetSize: DpSize) = it.adjustBySize(widgetSize)

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val prefs = currentState<Preferences>()
      val widgetSize = LocalSize.current

      val state by
          produceState<WidgetData?>(initialValue = null, prefs) {
            val sources = loadSources(prefs) ?: return@produceState
            value = WidgetManager.createData(sources)
          }

      Column(
          modifier =
              GlanceModifier.fillMaxSize()
                  .background(
                      ColorProvider(day = WidgetBackgroundLight, night = WidgetBackgroundDark)
                  )
                  .padding(8.dp)
                  .clickable(actionStartActivity<MainActivity>())
      ) {
        state?.let {
          val displayOption = displayOption(it.option, widgetSize)
          Content(it, displayOption)
        }
      }
    }
  }

  @Composable abstract fun Content(state: WidgetData, option: Option)
}

class CombinedWidget : BaseWidget() {
  @Composable
  override fun Content(state: WidgetData, option: Option) {
    WidgetDayDetails(state.dateInfo, option)
    Spacer(modifier = GlanceModifier.height(8.dp))
    WidgetMonthGrid(state.monthInfo, option)
  }
}

class DayDetailsWidget : BaseWidget() {
  override fun displayOption(it: Option, widgetSize: DpSize): Option = it

  @Composable
  override fun Content(state: WidgetData, option: Option) {
    WidgetDayDetails(state.dateInfo, option)
  }
}

class MonthGridWidget : BaseWidget() {
  override fun displayOption(it: Option, widgetSize: DpSize): Option = it

  @Composable
  override fun Content(state: WidgetData, option: Option) {
    val dateInfo = state.dateInfo
    if (dateInfo != null) {
      val fontScale = LocalContext.current.resources.configuration.fontScale
      Row(
          modifier = GlanceModifier.fillMaxWidth(),
          verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = GlanceModifier.defaultWeight()) {
          Text(
              text = dateInfo.value.year.toString(),
              style =
                  TextStyle(
                      fontSize = 14.limitScaleSp(option, fontScale),
                      fontWeight = FontWeight.Bold,
                      color = widgetColor(null),
                  ),
          )
          if (option.dayDetail.japaneseDate) {
            Text(
                text = dateInfo.japaneseYear,
                style =
                    TextStyle(
                        fontSize = 11.limitScaleSp(option, fontScale),
                        color = widgetColor(dateInfo.colorOfJapaneseYear, true),
                    ),
            )
          }
          if (option.dayDetail.lunarDate) {
            Text(
                text = dateInfo.lunarYear,
                style =
                    TextStyle(
                        fontSize = 11.limitScaleSp(option, fontScale),
                        color = widgetColor(null, true),
                    ),
            )
          }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = dateInfo.value.dayOfMonth.toString(),
              style =
                  TextStyle(
                      fontSize = 14.limitScaleSp(option, fontScale),
                      fontWeight = FontWeight.Bold,
                      color = widgetColor(dateInfo.colorOfDay),
                  ),
          )
          Text(
              text = dateInfo.value.dayOfWeek.displayName,
              style =
                  TextStyle(
                      fontSize = 12.limitScaleSp(option, fontScale),
                      color = widgetColor(dateInfo.value.dayOfWeek.color),
                  ),
          )
        }

        Column(
            modifier = GlanceModifier.defaultWeight(),
            horizontalAlignment = Alignment.End,
        ) {
          Text(
              text = DateInfo.run { dateInfo.value.monthName },
              style =
                  TextStyle(
                      fontSize = 14.limitScaleSp(option, fontScale),
                      fontWeight = FontWeight.Bold,
                      textAlign = TextAlign.End,
                      color = widgetColor(null),
                  ),
          )
          if (option.dayDetail.lunarDate) {
            Text(
                text = dateInfo.lunarDate.month.displayName,
                style =
                    TextStyle(
                        fontSize = 11.limitScaleSp(option, fontScale),
                        color = widgetColor(null, true),
                    ),
            )
          }
        }
      }
    }
    WidgetMonthGrid(state.monthInfo, option)
  }
}
