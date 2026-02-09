package com.vtl.holidaycalendar.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.TextAlign
import com.vtl.holidaycalendar.presentation.model.DateInfo
import com.vtl.holidaycalendar.presentation.model.Option

@Composable
fun WidgetDayDetails(dateInfo: DateInfo?, option: Option) {
    if (dateInfo == null) return


    Column(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Line 1: Year (Era) [space] Day number (Weekday) [space] Month
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Year
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = dateInfo.year.value,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = widgetColor(dateInfo.year.color))
                )
                if(option.japaneseInfo) {
                    dateInfo.japaneseDate.year?.let {
                        Text(
                            text = it.value,
                            style = TextStyle(fontSize = 11.sp, color = widgetColor(it.color,true))
                        )
                    }
                }
                dateInfo.lunarDate.year?.let {
                    Text(
                        text = it.value,
                        style = TextStyle(fontSize = 11.sp, color = widgetColor(it.color,true))
                    )
                }
            }

            // Day number (Weekday)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = dateInfo.day.value,
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = widgetColor(dateInfo.day.color)
                    )
                )
                Text(
                    text = dateInfo.weekday.value,
                    style = TextStyle(fontSize = 14.sp, color = widgetColor(dateInfo.weekday.color))
                )
            }

            // Month
            Column(modifier = GlanceModifier.defaultWeight(), horizontalAlignment = Alignment.End) {
                Text(
                    text = dateInfo.month.value,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = widgetColor(dateInfo.month.color),
                        textAlign = TextAlign.End
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
                dateInfo.lunarDate.month?.let {
                    Text(
                        text = it.value,
                        style = TextStyle(fontSize = 11.sp, color = widgetColor(it.color,true))
                    )
                }
            }
        }

        // Line 2: japanese holiday if has
        if(option.japaneseInfo) {
            dateInfo.japaneseDate.holiday?.let {
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = it.value,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = widgetColor(it.color)
                    )
                )
            }
        }
        
        // Line 3: lunar full date
        dateInfo.lunarDate.fullDisplayDay?.let {
            Spacer(modifier = GlanceModifier.height(2.dp))
            Text(
                text = it.value,
                style = TextStyle(fontSize = 11.sp, color = widgetColor(it.color,true))
            )
        }
        
        // Line 4: observance if has
        if(option.observance) {
            dateInfo.lunarDate.observance?.let {
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = it.value,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = widgetColor(it.color)
                    )
                )
            }
        }
        
        // Line 5: lucDieu full display
        if(option.lucDieu) {
            dateInfo.lunarDate.lucDieuFullDisplay?.let {
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = it.value,
                    style = TextStyle(fontSize = 11.sp, color = widgetColor(it.color))
                )
            }
            // Line 6: auspiciousHours
            dateInfo.lunarDate.auspiciousHours?.let {
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = it.value,
                    style = TextStyle(fontSize = 10.sp, color = widgetColor(it.color), textAlign = TextAlign.Center),
                    maxLines = 2
                )
            }
        }
    }
}
