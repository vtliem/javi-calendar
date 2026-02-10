package com.vtl.javicalendar.data.datasource

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HolidayRemoteDataSource {
  companion object {
    // https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html
    private const val CSV_URL = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv"
    private const val TAG = "HolidayRemoteDataSource"
  }

  /**
   * Fetches holiday data if it has changed since the last fetch. Returns null if data is up to date
   * or if an error occurs.
   */
  suspend fun fetch(lastModified: Long): HolidayData? =
      withContext(Dispatchers.IO) {
        try {
          if (detectChanges(lastModified)) {
            fetchNewData(lastModified)
          } else {
            null
          }
        } catch (e: Exception) {
          Log.e(TAG, "Failed to fetch data", e)
          null
        }
      }

  /** Checks if the remote data has changed using a HEAD request. */
  private fun detectChanges(lastModified: Long): Boolean {
    if (lastModified == 0L) return true

    val timeout = 10_000 // 10 seconds for refresh check
    return try {
      val url = URL(CSV_URL)
      val connection = url.openConnection() as HttpURLConnection
      connection.apply {
        requestMethod = "HEAD"
        connectTimeout = timeout
        readTimeout = timeout
        ifModifiedSince = lastModified
        connect()
      }

      val responseCode = connection.responseCode
      val serverLastModified = connection.lastModified

      val noUpdate =
          responseCode == HttpURLConnection.HTTP_NOT_MODIFIED ||
              (serverLastModified in 1..lastModified)

      if (noUpdate) {
        Log.v(TAG, "No update needed (Last-Modified: $lastModified)")
        false
      } else {
        true
      }
    } catch (e: Exception) {
      Log.w(TAG, "Failed to check Last-Modified, attempting full fetch: ${e.message}")
      true // Fallback to fetching if check fails
    }
  }

  /** Downloads the new CSV content. */
  private fun fetchNewData(lastModified: Long): HolidayData? {
    val timeout = if (lastModified == 0L) 300_000 else 10_000 // 5 mins for initial, 10s for refresh
    return try {
      val url = URL(CSV_URL)
      val connection = url.openConnection() as HttpURLConnection
      connection.apply {
        requestMethod = "GET"
        connectTimeout = timeout
        readTimeout = timeout
        if (lastModified > 0) {
          ifModifiedSince = lastModified
        }
        connect()
      }

      if (connection.responseCode == HttpURLConnection.HTTP_OK) {
        val newLastModified = connection.lastModified
        Log.v(TAG, "Fetched new data. Last-Modified: $newLastModified")

        val inputStream = connection.inputStream
        // The official Japanese government CSV is Shift-JIS encoded
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("Shift-JIS")))
        val content = reader.use { it.readText() }

        HolidayData(newLastModified, content)
      } else {
        Log.e(TAG, "Failed to fetch data: ${connection.responseCode} ${connection.responseMessage}")
        null
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error during fetchNewData", e)
      null
    }
  }
}
