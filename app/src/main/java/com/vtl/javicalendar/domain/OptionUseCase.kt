package com.vtl.javicalendar.domain

import com.vtl.javicalendar.data.datasource.OptionDataSource
import com.vtl.javicalendar.presentation.model.Option
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate

class OptionUseCase(private val dataSource: OptionDataSource) {
  private val _option = MutableStateFlow(dataSource.loadOption())
  private val option: StateFlow<Option> = _option.asStateFlow()

  operator fun invoke() = option

  fun updateOption(newOption: Option) =
      (_option.getAndUpdate { newOption } != newOption).also {
        if (it) {
          dataSource.saveOption(newOption)
        }
      }
}
