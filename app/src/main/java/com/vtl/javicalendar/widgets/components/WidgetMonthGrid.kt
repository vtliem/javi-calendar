package com.vtl.javicalendar.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.vtl.javicalendar.presentation.model.DateInfo
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.color
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.shortName
import com.vtl.javicalendar.presentation.model.MonthInfo
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.presentation.model.ZodiacDisplay

@Composable
fun WidgetMonthGrid(monthInfo: MonthInfo?, option: Option) {
  if (monthInfo == null) return

  // Weekday Headers
  Row(modifier = GlanceModifier.fillMaxWidth().padding(bottom = 2.dp)) {
    monthInfo.daysOfWeek.forEach { dayOfWeek ->
      Text(
          text = dayOfWeek.shortName,
          style =
              TextStyle(
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = widgetColor(dayOfWeek.color),
                  textAlign = TextAlign.Center,
              ),
          modifier = GlanceModifier.defaultWeight(),
      )
    }
  }
  Spacer(modifier = GlanceModifier.height(2.dp))
  Box(
      modifier = GlanceModifier.fillMaxWidth().height(0.5.dp).background(GlanceTheme.colors.outline)
  ) {}
  Spacer(modifier = GlanceModifier.height(2.dp))
  Column(modifier = GlanceModifier.fillMaxWidth()) {
    monthInfo.weeks.forEach { week ->
      Row(modifier = GlanceModifier.fillMaxWidth()) {
        week.forEach { dateInfo ->
          if (dateInfo == null) {
            Box(modifier = GlanceModifier.defaultWeight().padding(1.dp).height(32.dp)) {}
          } else {
            WidgetDayCell(dateInfo, option)
          }
        }
      }
    }
  }
}

@Composable
private fun RowScope.WidgetDayCell(dateInfo: DateInfo, option: Option) {
  val fontScale = LocalContext.current.resources.configuration.fontScale
  val noAdditionalData = !dateInfo.hasAdditionalData(option.month)
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalAlignment = Alignment.Top,
      modifier = GlanceModifier.defaultWeight().padding(1.dp),
  ) {
    // Line 1: Solar Day + Lunar Day
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      if (option.month.lunarDate && !noAdditionalData) {
        Text(
            text = dateInfo.lunarDayOfMonth,
            style =
                TextStyle(
                    fontSize = 11.sp,
                    color = widgetColor(dateInfo.colorOfLunarDay(option.month.zodiac), true),
                    textAlign = TextAlign.End,
                ),
        )
      }
      Spacer(modifier = GlanceModifier.width(4.dp))

      Text(
          text = dateInfo.value.dayOfMonth.toString(),
          style =
              TextStyle(
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = widgetColor(dateInfo.colorOfDay),
                  textAlign = TextAlign.Center,
              ),
          modifier =
              dateInfo.backgroundColorOfDay?.let {
                GlanceModifier.background(widgetColor(it))
                    .size((18.sp.value * fontScale).dp)
                    .cornerRadius((9.sp.value * fontScale).dp)
              } ?: GlanceModifier,
      )
    }

    // Line 2: Japanese Holiday
    if (option.month.japaneseDate) {
      dateInfo.japaneseHoliday?.let {
        Text(
            text = it,
            style =
                TextStyle(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = widgetColor(dateInfo.colorOfJapaneseHoliday),
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
        )
      }
    }

    // Line 3: Zodiac
    if (option.month.lunarDate && option.month.zodiac == ZodiacDisplay.Full) {
      Text(
          text = dateInfo.lunarDate.zodiac.zodiacName,
          style =
              TextStyle(
                  fontSize = 8.sp,
                  color = widgetColor(dateInfo.lunarDate.zodiac.color, true),
                  textAlign = TextAlign.Center,
              ),
          maxLines = 1,
      )
    }

    // Line 4: Observance
    if (option.month.lunarDate && option.month.observance) {
      dateInfo.lunarDate.observance?.let {
        Text(
            text = it,
            style =
                TextStyle(
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = widgetColor(dateInfo.colorOfObservance),
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
        )
      }
    }
    if (noAdditionalData) {
      if (option.month.lunarDate) {
        Text(
            text = dateInfo.lunarDayOfMonth,
            style =
                TextStyle(
                    fontSize = 11.sp,
                    color = widgetColor(dateInfo.colorOfLunarDay(option.month.zodiac), true),
                    textAlign = TextAlign.End,
                ),
        )
      } else {
        Spacer(modifier = GlanceModifier.height(10.dp))
      }
    }
  }
}
