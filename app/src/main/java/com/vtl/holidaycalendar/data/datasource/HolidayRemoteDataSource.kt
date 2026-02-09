package com.vtl.holidaycalendar.data.datasource

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class HolidayRemoteDataSource {
    companion object{
        private const val CSV_URL = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv"
    }
    suspend fun fetch(etag: String?): HolidayData? = withContext(Dispatchers.IO) {
        val timeout = if (etag == null) 300_000 else 10_000 // 5 minutes for initial, 10 seconds for refresh
        try {
            val url = URL(CSV_URL)
            
            // First, check with a HEAD request
            val headConnection = url.openConnection() as HttpURLConnection
            headConnection.requestMethod = "HEAD"
            headConnection.connectTimeout = timeout
            headConnection.readTimeout = timeout
            if (etag != null) {
                headConnection.setRequestProperty("If-None-Match", etag)
            }
            headConnection.connect()

            val responseCode = headConnection.responseCode
            val newEtag = headConnection.getHeaderField("ETag")
            
            if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED || (etag != null && etag == newEtag)) {
                Log.v("HolidayRemoteDataSource", "No update needed")
                return@withContext null
            }

            // If changed or no etag, download content
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
            if (etag != null) {
                connection.setRequestProperty("If-None-Match", etag)
            }
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val finalEtag = connection.getHeaderField("ETag") ?: newEtag
                Log.v("HolidayRemoteDataSource", "Fetched data with etag: $finalEtag")
                val inputStream = connection.inputStream
                // The official CSV is Shift-JIS encoded
                val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("Shift-JIS")))
                val content = reader.use { it.readText() }
                return@withContext HolidayData(finalEtag, content)
            }
            Log.e("HolidayRemoteDataSource", "Failed to fetch data: ${connection.responseMessage}")
            null
        } catch (e: Exception) {
            Log.e("HolidayRemoteDataSource", "Failed to fetch data", e)
            null
        }
    }
}
