package com.vtl.javicalendar.data.datasource

import java.io.IOException

sealed class FetchHolidayError(message: String, cause: Throwable?) : IOException(message, cause) {
  class NetworkError(cause: Throwable) : FetchHolidayError(cause.javaClass.simpleName, cause)

  class ServerError(val code: Int, message: String) : FetchHolidayError(message, null) {
    override fun toString(): String {
      return "ServerError(code=$code, message='$message')"
    }
  }

  class UnknownError(cause: Throwable) : FetchHolidayError(cause.javaClass.simpleName, cause)

  val type =
      when (this) {
        is NetworkError -> HolidayErrorType.Network
        is ServerError -> HolidayErrorType.Server
        else -> HolidayErrorType.Unknown
      }
}

enum class HolidayErrorType {
  Network,
  Server,
  Parse,
  Unknown;

  companion object {
    fun of(name: String?) = entries.find { it.name == name }
  }
}
