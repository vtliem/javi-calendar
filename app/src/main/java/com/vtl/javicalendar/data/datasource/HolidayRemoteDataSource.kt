package com.vtl.javicalendar.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpHeaders
import io.ktor.http.lastModified
import java.net.HttpURLConnection
import kotlin.getValue

class HolidayRemoteDataSource {
  companion object {
    const val INFO_URL = "https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html"
    const val CSV_URL = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv"

    private val HttpResponse.lastModified: Long
      get() = lastModified()?.toInstant()?.toEpochMilli() ?: 0L
  }

  private val client by lazy {
    HttpClient(Android) {
      engine {
        connectTimeout = 10_000
        socketTimeout = 10_000
      }
      install(io.ktor.client.plugins.DefaultRequest) {
        header(HttpHeaders.UserAgent, "Mozilla/5.0 (Android)")
      }
    }
  }

  /**
   * Fetch Japanese Holiday csv data from [HolidayRemoteDataSource.CSV_URL]
   *
   * @return
   * - null: No updated need (data unchanged from last update)
   * - HolidayData: Updated data
   *
   * @throws FetchHolidayError
   */
  suspend fun fetch(lastModified: Long): HolidayData? {
    return if (detectChanges(lastModified)) {
      fetchNewData()
    } else {
      null
    }
  }

  /** Checks if the remote data has changed using a HEAD request. */
  private suspend fun detectChanges(lastModified: Long): Boolean {
    if (lastModified == 0L) return true
    val response =
        try {
          client.head(CSV_URL)
        } catch (t: Throwable) {
          throw FetchHolidayError.NetworkError(t)
        }
    if (response.status.value != HttpURLConnection.HTTP_OK)
        throw FetchHolidayError.ServerError(response.status.value, response.status.description)

    return response.lastModified.let { it == 0L || it != lastModified }
  }

  /** Downloads the new CSV content. */
  private suspend fun fetchNewData(): HolidayData {
    val response =
        try {
          client.get(CSV_URL)
        } catch (t: Throwable) {
          throw FetchHolidayError.NetworkError(t)
        }
    if (response.status.value != HttpURLConnection.HTTP_OK)
        throw FetchHolidayError.ServerError(response.status.value, response.status.description)
    val lastModified = response.lastModified
    val content = String(response.readRawBytes(), charset("Shift-JIS"))
    return HolidayData(lastModified = lastModified, content = content)
  }
}
