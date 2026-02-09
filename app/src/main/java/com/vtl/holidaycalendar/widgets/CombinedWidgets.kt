package com.vtl.holidaycalendar.widgets

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.color.ColorProvider
import androidx.glance.background
import androidx.glance.layout.*
import com.vtl.holidaycalendar.MainActivity
import com.vtl.holidaycalendar.data.datasource.HolidayDataSource
import com.vtl.holidaycalendar.domain.CalendarFactory
import com.vtl.holidaycalendar.domain.model.JapaneseHolidays
import com.vtl.holidaycalendar.presentation.model.DateInfo
import com.vtl.holidaycalendar.presentation.model.MonthInfo
import com.vtl.holidaycalendar.presentation.model.Option
import com.vtl.holidaycalendar.presentation.theme.*
import com.vtl.holidaycalendar.widgets.components.WidgetDayDetails
import com.vtl.holidaycalendar.widgets.components.WidgetMonthGrid
import java.time.LocalDate

class CombinedWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val today = LocalDate.now()
        val dataSource = HolidayDataSource(context)
        val holidays = JapaneseHolidays.parseHolidays(dataSource.getFromCache() ?: "")
        val monthInfo = CalendarFactory.createMonthInfo(today.year, today.monthValue, holidays, today)
        val dateInfo = monthInfo.getDate(today)

        val option = Option()
        provideContent {
            Log.v("CombinedWidget","size: ${LocalSize.current}, ${context.resources.configuration.orientation}")
            CombinedWidgetContent(monthInfo, dateInfo,option.adjustBySize(LocalSize.current) )
        }
    }

    @Composable
    private fun CombinedWidgetContent(monthInfo: MonthInfo, dateInfo: DateInfo?, option: Option) {
        Column(
            modifier = GlanceModifier
                .wrapContentSize()
                .background(ColorProvider(day = WidgetBackgroundLight, night = WidgetBackgroundDark))
                .padding(8.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            WidgetDayDetails(dateInfo,option)
            
            Spacer(modifier = GlanceModifier.height(8.dp))
            
            // Divider
            Box(modifier = GlanceModifier.fillMaxWidth().height(1.dp).background( GlanceTheme.colors.outline)) {}
            
            Spacer(modifier = GlanceModifier.height(8.dp))

            WidgetMonthGrid(monthInfo,option)
        }
    }
}
