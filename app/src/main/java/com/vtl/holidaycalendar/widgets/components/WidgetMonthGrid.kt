package com.vtl.holidaycalendar.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.appwidget.cornerRadius
import androidx.glance.text.TextAlign
import com.vtl.holidaycalendar.presentation.model.DateInfo
import com.vtl.holidaycalendar.presentation.model.MonthInfo
import com.vtl.holidaycalendar.presentation.model.Option

@Composable
fun WidgetMonthGrid(monthInfo: MonthInfo?, option: Option) {
    if (monthInfo == null) return

    // Weekday Headers
    Row(modifier = GlanceModifier.fillMaxWidth().padding(bottom = 2.dp)) {
        monthInfo.weekDayNames.forEach { dayField ->
            Text(
                text = dayField.value,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = widgetColor(dayField.color),
                    textAlign = TextAlign.Center
                ),
                modifier = GlanceModifier.defaultWeight()
            )
        }
    }
    Spacer(modifier = GlanceModifier.height(2.dp))
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(GlanceTheme.colors.outline)
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.Top,
        modifier = GlanceModifier.defaultWeight().padding(1.dp).height(32.dp)
    ) {
        // Line 1: Solar Day
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            dateInfo.lunarDate.day?.let {
                Text(
                    text = it.value,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = GlanceTheme.colors.secondary,
                        textAlign = TextAlign.End
                    )
                )
            }

            Spacer(modifier = GlanceModifier.width(8.dp))

            Text(
                text = dateInfo.day.value,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = widgetColor(dateInfo.day.color),
                    textAlign = TextAlign.Center
                ),
                modifier = dateInfo.day.backgroundColor?.let {
                    GlanceModifier
                        .background(widgetColor(dateInfo.day.backgroundColor))
                        .size(
                            (18.sp.value * fontScale).dp
                        )
                        .cornerRadius((9.sp.value * fontScale).dp)
                } ?: GlanceModifier
            )
        }

        // Line 2: Lunar Day Name (lucDieu)
        if (option.monthLucDieu) {
            dateInfo.lunarDate.lucDieu?.let {
                Text(
                    text = it.value,
                    style = TextStyle(
                        fontSize = 7.sp,
                        color = widgetColor(it.color),
                        textAlign = TextAlign.End
                    )
                )
            }
        }
        if (option.monthJapaneseHoliday && option.japaneseInfo) {
            // Line 3: Observance or Japanese Holiday
            dateInfo.japaneseDate.holiday?.let {
                Text(
                    text = it.value,
                    style = TextStyle(
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = widgetColor(it.color),
                        textAlign = TextAlign.End
                    )
                )
            }
        }
        if (option.monthObservance) {
            dateInfo.lunarDate.observance?.let {
                Text(
                    text = it.value,
                    style = TextStyle(
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = widgetColor(it.color),
                        textAlign = TextAlign.End
                    )
                )
            }
        }
    }
}
