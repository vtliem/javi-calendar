package com.vtl.holidaycalendar.presentation.model

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

data class Option(
    val japaneseInfo: Boolean = true,
    val lucDieu: Boolean = true,
    val observance: Boolean = true,
    val monthLucDieu: Boolean = true,
    val monthJapaneseHoliday: Boolean = true,
    val monthObservance: Boolean = true
){
    fun adjustBySize(widgetSize: DpSize) = copy(
        monthLucDieu = monthLucDieu && widgetSize.width >= 280.dp,
        monthJapaneseHoliday = monthJapaneseHoliday && widgetSize.width >= 300.dp && widgetSize.height >=300.dp,
        monthObservance = monthObservance && widgetSize.width >= 300.dp && widgetSize.height >=300.dp,
    )
}