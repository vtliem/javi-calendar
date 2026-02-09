package com.vtl.holidaycalendar.widgets

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
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
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.vtl.holidaycalendar.MainActivity
import com.vtl.holidaycalendar.presentation.model.DateInfo
import com.vtl.holidaycalendar.presentation.model.MonthInfo
import com.vtl.holidaycalendar.presentation.model.Option
import com.vtl.holidaycalendar.presentation.theme.*
import com.vtl.holidaycalendar.widgets.components.WidgetDayDetails
import com.vtl.holidaycalendar.widgets.components.WidgetMonthGrid
import kotlinx.serialization.json.Json
import java.time.LocalDate

class CombinedWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val monthJson = prefs[stringPreferencesKey("month_data")]
            val optionJson = prefs[stringPreferencesKey("option_data")]

            val today = LocalDate.now()

            // Fallback if data is missing from state
            val monthInfo = monthJson?.let {
                runCatching { json.decodeFromString<MonthInfo>(it) }.getOrNull()
            }
            val option = optionJson?.let {
                runCatching { json.decodeFromString<Option>(it) }.getOrNull()
            } ?: Option()

            val dateInfo = monthInfo?.getDate(today)
            val displayOption = option.adjustBySize(LocalSize.current)
            Log.v("CombinedWidget", "${LocalSize.current} $displayOption")
            CombinedWidgetContent(monthInfo, dateInfo, displayOption)
        }
    }

    @Composable
    private fun CombinedWidgetContent(monthInfo: MonthInfo?, dateInfo: DateInfo?, option: Option) {
        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(ColorProvider(day = WidgetBackgroundLight, night = WidgetBackgroundDark))
                .padding(8.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            WidgetDayDetails(dateInfo, option)

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Divider
            Box(modifier = GlanceModifier.fillMaxWidth().height(1.dp).background( GlanceTheme.colors.outline)) {}

            Spacer(modifier = GlanceModifier.height(8.dp))

            WidgetMonthGrid(monthInfo, option)
        }
    }
}
