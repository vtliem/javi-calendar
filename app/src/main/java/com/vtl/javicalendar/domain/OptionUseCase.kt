package com.vtl.javicalendar.domain

import android.content.Context
import com.vtl.javicalendar.data.datasource.OptionDataSource
import com.vtl.javicalendar.presentation.model.Option
import com.vtl.javicalendar.widgets.WidgetManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate

class OptionUseCase(
    private val dataSource: OptionDataSource,
    private val context: Context
) {
    private val _option = MutableStateFlow(dataSource.loadOption())
    val option: StateFlow<Option> = _option.asStateFlow()

    suspend fun updateOption(newOption: Option) {
        val oldOption = _option.getAndUpdate { newOption }
        
        if (oldOption != newOption) {
            dataSource.saveOption(newOption)
            WidgetManager.triggerUpdate(context)
        }
    }
}
