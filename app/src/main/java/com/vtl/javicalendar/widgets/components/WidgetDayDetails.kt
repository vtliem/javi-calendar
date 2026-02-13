package com.vtl.javicalendar.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.vtl.javicalendar.presentation.model.DateInfo
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.color
import com.vtl.javicalendar.presentation.model.DateInfo.Companion.displayName
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.presentation.model.ZodiacDisplay

@Composable
fun WidgetDayDetails(dateInfo: DateInfo?, option: Option) {
  if (dateInfo == null) return

  Column(
      modifier = GlanceModifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // Line 1: Year (Era) [space] Day number (Weekday) [space] Month
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
      // Year
      Column(modifier = GlanceModifier.defaultWeight()) {
        Text(
            text = dateInfo.value.year.toString(),
            style =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = widgetColor(null),
                ),
        )
        if (option.dayDetail.japaneseDate) {
          Text(
              text = dateInfo.japaneseYear,
              style =
                  TextStyle(
                      fontSize = 11.sp,
                      color = widgetColor(dateInfo.colorOfJapaneseYear, true),
                  ),
          )
        }
        if (option.dayDetail.lunarDate) {
          Text(
              text = dateInfo.lunarYear,
              style = TextStyle(fontSize = 11.sp, color = widgetColor(null, true)),
          )
        }
      }

      // Day number (Weekday)
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = dateInfo.value.dayOfMonth.toString(),
            style =
                TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = widgetColor(dateInfo.colorOfDay),
                ),
        )
        Text(
            text = dateInfo.value.dayOfWeek.displayName,
            style =
                TextStyle(fontSize = 14.sp, color = widgetColor(dateInfo.value.dayOfWeek.color)),
        )
      }

      // Month
      Column(
          modifier = GlanceModifier.defaultWeight(),
          horizontalAlignment = Alignment.End,
      ) {
        Text(
            text = DateInfo.run { dateInfo.value.monthName },
            style =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    color = widgetColor(null),
                ),
        )
        if (option.dayDetail.lunarDate) {
          Text(
              text = dateInfo.lunarDate.month.displayName,
              style = TextStyle(fontSize = 11.sp, color = widgetColor(null, true)),
          )
        }
      }
    }

    // Line 2: japanese holiday if has
    if (option.dayDetail.japaneseDate) {
      dateInfo.japaneseHoliday?.let {
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text(
            text = it,
            style =
                TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = widgetColor(dateInfo.colorOfJapaneseHoliday),
                ),
        )
      }
    }

    // Line 3: lunar day
    if (option.dayDetail.lunarDate) {
      Spacer(modifier = GlanceModifier.height(2.dp))
      Text(
          text = dateInfo.lunarDate.day.displayName,
          style =
              TextStyle(
                  fontSize = 11.sp,
                  color = widgetColor(dateInfo.colorOfLunarDay(option.dayDetail.zodiac), true),
              ),
      )
    }

    // zodiac
    if (option.dayDetail.lunarDate && option.dayDetail.zodiac == ZodiacDisplay.Full) {
      Spacer(modifier = GlanceModifier.height(2.dp))
      Text(
          text = dateInfo.lunarDate.zodiac.toString(),
          style = TextStyle(fontSize = 11.sp, color = widgetColor(dateInfo.lunarDate.zodiac.color)),
          modifier = GlanceModifier.padding(start = 8.dp),
      )
      Text(
          text = dateInfo.lunarDate.zodiac.detail,
          style = TextStyle(fontSize = 10.sp, color = widgetColor(dateInfo.lunarDate.zodiac.color)),
      )
      // Line 6: auspiciousHours
      Text(
          text = dateInfo.lunarDate.auspiciousHours,
          style =
              TextStyle(
                  fontSize = 10.sp,
                  color = widgetColor(dateInfo.colorOfAuspiciousHours),
                  textAlign = TextAlign.Center,
              ),
          maxLines = 2,
      )
    }

    // observance if has
    if (option.dayDetail.lunarDate && option.dayDetail.observance) {
      dateInfo.lunarDate.observance?.let {
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text(
            text = it,
            style =
                TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = widgetColor(dateInfo.colorOfObservance),
                ),
        )
      }
    }
  }
}
