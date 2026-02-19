package com.vtl.javicalendar.data.repository

import android.util.Log
import com.vtl.javicalendar.data.datasource.FetchHolidayError
import com.vtl.javicalendar.data.datasource.HolidayData
import com.vtl.javicalendar.data.datasource.HolidayErrorType
import com.vtl.javicalendar.data.datasource.HolidayLocalDataSource
import com.vtl.javicalendar.data.datasource.HolidayRemoteDataSource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import java.io.IOException
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class HolidayRepositoryImplTest {

  private val localDataSource: HolidayLocalDataSource = mockk(relaxed = true)
  private val remoteDataSource: HolidayRemoteDataSource = mockk()

  @BeforeEach
  fun setUp() {
    mockkStatic(Log::class)
    every { Log.v(any<String>(), any<String>()) } returns 0
    every { Log.w(any<String>(), any<String>()) } returns 0
    every { Log.w(any<String>(), any<String>(), any()) } returns 0
    every { Log.e(any<String>(), any<String>(), any()) } returns 0
  }

  @Test
  @DisplayName("getHolidays should return remote data and save to local if there is new data")
  fun fetchNewData() = runTest {
    val repository = HolidayRepositoryImpl(localDataSource, HolidayRemoteDataSource())
    val lastModified = 0L
    every { localDataSource.loadLastModified() } returns lastModified

    val result = repository.getHolidays()

    assertTrue(result.lastModified > 0)
    assertTrue(result.lastSuccess > 0)
    assertFalse(result.years.isEmpty())
    verify(exactly = 1) { localDataSource.saveData(any()) }
    verify(exactly = 0) { localDataSource.saveSuccess() }
    verify(exactly = 0) { localDataSource.loadData() }
    verify(exactly = 0) { localDataSource.saveError(any(), any()) }

    // no fetching
    every { localDataSource.loadLastModified() } returns result.lastModified
    repository.getHolidays()
    verify(exactly = 1) { localDataSource.saveData(any()) }
    verify(exactly = 1) { localDataSource.saveSuccess() }
    verify(exactly = 1) { localDataSource.loadData() }
    verify(exactly = 0) { localDataSource.saveError(any(), any()) }
  }

  @Test
  @DisplayName("getHolidays should handle FetchHolidayError and fallback to local")
  fun fetchDatFailed() = runTest {
    val repository = HolidayRepositoryImpl(localDataSource, remoteDataSource)
    val lastModified = 100L
    val localData = HolidayData(lastModified = 100L, content = "2024/01/01,Cached Holiday")
    every { localDataSource.loadLastModified() } returns lastModified
    coEvery { remoteDataSource.fetch(lastModified) } throws
        FetchHolidayError.NetworkError(IOException())
    every { localDataSource.loadData() } returns localData

    val result = repository.getHolidays()

    assertEquals(100L, result.lastModified)
    assertEquals("Cached Holiday", result.getHoliday(LocalDate.of(2024, 1, 1)))
    verify { localDataSource.saveError(HolidayErrorType.Network, false) }
  }

  @Test
  @DisplayName("getHolidays should handle parsing error and fallback to local")
  fun parseDataFailed() = runTest {
    val repository = HolidayRepositoryImpl(localDataSource, remoteDataSource)

    val lastModified = 100L
    val invalidData = HolidayData(lastModified = 200L, content = "invalid,csv")
    val localData = HolidayData(lastModified = 100L, content = "2024/01/01,Local")

    every { localDataSource.loadLastModified() } returns lastModified
    coEvery { remoteDataSource.fetch(lastModified) } returns invalidData
    every { localDataSource.loadData() } returns localData

    val result = repository.getHolidays()

    assertNotNull(result)
    assertNotNull(result.getHoliday(LocalDate.of(2024, 1, 1)))
  }

  @Test
  @DisplayName("getHolidays fallback to default when parsing local data failed")
  fun parseLocalDataFailed() = runTest {
    val repository = HolidayRepositoryImpl(localDataSource, remoteDataSource)
    val lastModified = 100L
    val localData = HolidayData(lastModified = 100L, content = "2024/01/01aa,Local")

    every { localDataSource.loadLastModified() } returns lastModified
    coEvery { remoteDataSource.fetch(lastModified) } returns null
    every { localDataSource.loadData() } returns localData

    val result = repository.getHolidays()

    assertNotNull(result)
    assertNotNull(result.years.isEmpty())
  }
}
