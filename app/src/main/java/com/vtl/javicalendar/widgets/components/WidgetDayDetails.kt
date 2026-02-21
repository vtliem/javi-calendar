package com.vtl.javicalendar.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
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
import com.vtl.javicalendar.utils.limitScaleSp

@Composable
fun WidgetDayDetails(dateInfo: DateInfo?, option: Option) {
  if (dateInfo == null) return
  val fontScale = LocalContext.current.resources.configuration.fontScale

  Column(
      modifier = GlanceModifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
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
                    fontSize = 32.limitScaleSp(option, fontScale),
                    fontWeight = FontWeight.Bold,
                    color = widgetColor(dateInfo.colorOfDay),
                ),
        )
        Text(
            text = dateInfo.value.dayOfWeek.displayName,
            style =
                TextStyle(
                    fontSize = 14.limitScaleSp(option, fontScale),
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

    if (option.dayDetail.japaneseDate) {
      dateInfo.japaneseLongHoliday?.let {
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text(
            text = it,
            style =
                TextStyle(
                    fontSize = 11.limitScaleSp(option, fontScale),
                    fontWeight = FontWeight.Bold,
                    color = widgetColor(dateInfo.colorOfJapaneseHoliday),
                ),
        )
      }
    }

    if (option.dayDetail.lunarDate) {
      Spacer(modifier = GlanceModifier.height(2.dp))
      Text(
          text = dateInfo.lunarDate.day.displayName,
          style =
              TextStyle(
                  fontSize = 11.limitScaleSp(option, fontScale),
                  color = widgetColor(dateInfo.colorOfLunarDay(option.dayDetail.zodiac), true),
              ),
      )
    }

    if (option.dayDetail.lunarDate && option.dayDetail.zodiac == ZodiacDisplay.Full) {
      WidgetZodiacDetail(dateInfo, option)
    }

    if (option.dayDetail.lunarDate && option.dayDetail.observance) {
      dateInfo.lunarDate.observance?.let {
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text(
            text = it,
            style =
                TextStyle(
                    fontSize = 11.limitScaleSp(option, fontScale),
                    fontWeight = FontWeight.Bold,
                    color = widgetColor(dateInfo.colorOfObservance),
                ),
        )
      }
    }
  }
}

@Composable
private fun WidgetZodiacDetail(dateInfo: DateInfo, option: Option) {
  val fontScale = LocalContext.current.resources.configuration.fontScale
  val zodiac = dateInfo.lunarDate.zodiac
  val duty = dateInfo.lunarDate.duty
  Column(modifier = GlanceModifier.fillMaxWidth()) {
    Spacer(modifier = GlanceModifier.height(2.dp))
    Text(
        text = zodiac.toString(),
        style =
            TextStyle(
                fontSize = 11.limitScaleSp(option, fontScale),
                color = widgetColor(zodiac.color, true),
                textAlign = TextAlign.Center,
            ),
        modifier = GlanceModifier.fillMaxWidth(),
    )
    Text(
        text = zodiac.detail,
        style =
            TextStyle(
                fontSize = 10.limitScaleSp(option, fontScale),
                color = widgetColor(null, true),
            ),
    )
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Trực: ",
            style =
                TextStyle(
                    fontSize = 8.limitScaleSp(option, fontScale),
                    color = widgetColor(null, true),
                ),
        )
        Text(
            text = duty.dutyName,
            style =
                TextStyle(fontSize = 10.limitScaleSp(option, fontScale), color = widgetColor(null)),
        )
        dateInfo.lunarDate.solarTermName?.let {
          Text(
              text = "Tiết khí:",
              style =
                  TextStyle(
                      fontSize = 8.limitScaleSp(option, fontScale),
                      color = widgetColor(null, true),
                  ),
              modifier = GlanceModifier.padding(start = 16.dp),
          )
          Text(
              text = it,
              style =
                  TextStyle(
                      fontSize = 10.limitScaleSp(option, fontScale),
                      color = widgetColor(null),
                  ),
          )
        }
      }
      if (duty.goodFor.isNotEmpty()) {
        Text(
            text = duty.goodFor,
            style =
                TextStyle(
                    fontSize = 10.limitScaleSp(option, fontScale),
                    color = widgetColor(null, true),
                ),
            modifier = GlanceModifier.padding(start = 12.dp),
        )
      }
      if (duty.badFor.isNotEmpty()) {
        Text(
            text = duty.badFor,
            style =
                TextStyle(
                    fontSize = 10.limitScaleSp(option, fontScale),
                    color = widgetColor(null, true),
                ),
            modifier = GlanceModifier.padding(start = 12.dp),
        )
      }
    }
    Row {
      Text(
          text = "Giờ Hoàng Đạo: ",
          style =
              TextStyle(
                  fontSize = 8.limitScaleSp(option, fontScale),
                  color = widgetColor(null, true),
              ),
      )
      Text(
          text = dateInfo.lunarDate.auspiciousHours,
          style =
              TextStyle(
                  fontSize = 10.limitScaleSp(option, fontScale),
                  color = widgetColor(dateInfo.colorOfAuspiciousHours),
              ),
      )
    }
  }
}
