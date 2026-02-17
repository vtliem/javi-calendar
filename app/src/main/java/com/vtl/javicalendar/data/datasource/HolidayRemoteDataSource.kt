package com.vtl.javicalendar.data.datasource

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.Charset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HolidayRemoteDataSource {
  companion object {
    const val INFO_URL = "https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html"
    const val CSV_URL = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv"

    /**
     * request japanese holiday csv data
     *
     * @param method `GET` or `HEAD`
     * @param onProcess
     *     - lastModified: Long
     *     - content: () -> String use to read response body, should call inside onProcess block
     *
     * @throws FetchHolidayError
     */
    private inline fun <reified R> request(
        method: String,
        timeout: Int,
        onProcess: (lastModified: Long, content: () -> String) -> R,
    ): R {
      val connection =
          (URI(CSV_URL).toURL().openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = timeout
            readTimeout = timeout
          }

      try {
        val responseCode =
            try {
              connection.responseCode
            } catch (e: IOException) {
              throw FetchHolidayError.NetworkError(e)
            } catch (e: Exception) {
              throw FetchHolidayError.UnknownError(e)
            }

        when {
          responseCode <= 0 -> {
            throw FetchHolidayError.NetworkError(IOException("Invalid response from server"))
          }

          responseCode == HttpURLConnection.HTTP_OK -> {
            return onProcess(connection.lastModified) {
              connection.inputStream.bufferedReader(Charset.forName("Shift-JIS")).use {
                it.readText()
              }
            }
          }

          else -> {
            throw FetchHolidayError.ServerError(responseCode, connection.responseMessage)
          }
        }
      } finally {
        runCatching {
          // ignore error
          connection.disconnect()
        }
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
  suspend fun fetch(lastModified: Long): HolidayData? =
      withContext(Dispatchers.IO) { fetchSync(lastModified) }

  fun fetchSync(lastModified: Long): HolidayData? {
    return if (detectChanges(lastModified)) {
      fetchNewData(lastModified)
    } else {
      null
    }
  }

  /** Checks if the remote data has changed using a HEAD request. */
  private fun detectChanges(lastModified: Long): Boolean {
    if (lastModified == 0L) return true
    // 10 seconds for refresh check
    return request("HEAD", 10_000) { remoteLastModified, _ ->
      remoteLastModified == 0L || remoteLastModified != lastModified
    }
  }

  /** Downloads the new CSV content. */
  private fun fetchNewData(lastModified: Long): HolidayData {
    val timeout = if (lastModified == 0L) 300_000 else 10_000 // 5 mins for initial, 10s for refresh
    return request("GET", timeout) { remoteLastModified, content ->
      val csv = content().trim()
      if (csv.isEmpty()) {
        throw FetchHolidayError.ServerError(HttpURLConnection.HTTP_OK, "Empty response")
      }
      HolidayData(remoteLastModified, csv)
    }
  }
}
