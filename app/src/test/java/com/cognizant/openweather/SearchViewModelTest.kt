package com.cognizant.openweather

import android.location.Location
import com.cognizant.openweather.data.prefs.SearchPreferences
import com.cognizant.openweather.data.repositories.LocationRepositoryImpl
import com.cognizant.openweather.data.repositories.WeatherRepositoryImpl
import com.cognizant.openweather.ui.screens.search.SearchViewModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * This is a test class for [SearchViewModel].
 */
@ExperimentalCoroutinesApi
class SearchViewModelTest {

    private lateinit var searchViewModel: SearchViewModel
    private val weatherRepository: WeatherRepositoryImpl = mockk(relaxed = true)
    private val locationRepository: LocationRepositoryImpl = mockk(relaxed = true)
    private val searchPreferences: SearchPreferences = mockk(relaxed = true)


    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        searchViewModel = SearchViewModel(weatherRepository, locationRepository, searchPreferences)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when onSearchQueryChanged is called, it updates searchQuery`() = runTest {
        // Arrange
        val newQuery = "New York"

        // Act
        searchViewModel.onSearchQueryChanged(newQuery)

        // Assert
        val updatedQuery = searchViewModel.searchQuery.value
        Assert.assertEquals(newQuery, updatedQuery)
    }

    @Test
    fun `when onSearchQueryChanged is called and there is an active errorMessage, it disappears`() =
        runTest {
            // Arrange
            val newQuery = "New York"
            searchViewModel.errorMessage.value = "Error"

            // Act
            searchViewModel.onSearchQueryChanged(newQuery)

            // Assert
            val updatedErrorMessage = searchViewModel.errorMessage.value
            Assert.assertNull(updatedErrorMessage)
        }

    @Test
    fun `when onSearchButtonClicked is called, it calls getWeather with the correct parameters`() =
        runTest {
            // Arrange
            val cityName = "London"

            searchViewModel.onSearchQueryChanged(cityName)

            coEvery {
                weatherRepository.getWeather(
                    cityName,
                    null,
                    null,
                )
            } returns mockk(relaxed = true)


            // Act
            searchViewModel.onSearchButtonClicked()

            // Assert
            coVerify {
                weatherRepository.getWeather(
                    cityName,
                    null,
                    null,
                )
            }
        }

    @Test
    fun `when loadGPSCoordinatesAndWeatherInfo is called, it calls getWeather with the correct parameters`() =
        runTest {
            // Arrange
            val location: Location = mockk()
            val latitude = 40.712776
            val longitude = -74.005974

            searchViewModel.locationAlreadyLoaded = false

            every { location.latitude } returns latitude
            every { location.longitude } returns longitude

            val task: Task<Location> = mockk(relaxed = true)
            every { task.addOnSuccessListener(any()) } returns task
            every { locationRepository.getLastLocation() } returns task

            // Act
            searchViewModel.loadGPSCoordinatesAndWeatherInfo()

            // manually trigger the success listener
            val captor = slot<OnSuccessListener<Location>>()
            verify { task.addOnSuccessListener(capture(captor)) }
            captor.captured.onSuccess(location)

            advanceUntilIdle()


            // Assert
            coVerify { // verify that the function was called
                weatherRepository.getWeather(
                    cityName = null,
                    latitude = latitude,
                    longitude = longitude,
                )
            }

        }

}