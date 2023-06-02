package com.cognizant.openweather

import com.cognizant.openweather.data.prefs.SearchPreferences
import com.cognizant.openweather.data.repositories.LocationRepositoryImpl
import com.cognizant.openweather.data.repositories.WeatherRepositoryImpl
import com.cognizant.openweather.network.weather.MockWeatherResponse
import com.cognizant.openweather.network.weather.WeatherResponse
import com.cognizant.openweather.ui.MainViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.invoke
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    private lateinit var mainViewModel: MainViewModel
    private val weatherRepository: WeatherRepositoryImpl = mockk(relaxed = true)
    private val locationRepository: LocationRepositoryImpl = mockk(relaxed = true)
    private val searchPreferences: SearchPreferences = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        mainViewModel = MainViewModel(weatherRepository, locationRepository, searchPreferences)
    }

    @Test
    fun `when onSearchQueryChanged is called, it updates searchQuery`() = runBlocking {
        // Arrange
        val newQuery = "New York"

        // Act
        mainViewModel.onSearchQueryChanged(newQuery)

        // Assert
        val updatedQuery = mainViewModel.searchQuery.value
        Assert.assertEquals(newQuery, updatedQuery)
    }

    fun `when onSearchQueryChanged is called and there is an active errorMessage, it disappears`() = runBlocking {
        // Arrange
        val newQuery = "New York"
        mainViewModel.errorMessage.value = "Error"

        // Act
        mainViewModel.onSearchQueryChanged(newQuery)

        // Assert
        val updatedErrorMessage = mainViewModel.errorMessage.value
        Assert.assertNull(updatedErrorMessage)
    }

    @Test
    fun `when navigateTo is called, it updates screen`() = runBlocking {
        // Arrange
        val newScreen = "New Screen"

        // Act
        mainViewModel.navigateTo(newScreen)

        // Assert
        val updatedScreen = mainViewModel.currentScreen.value
        Assert.assertEquals(newScreen, updatedScreen)

    }

    @Test
    fun `when onSearchButtonClicked is called, it calls getWeather with the correct parameters`() =
        runBlocking {
            // Arrange
            val cityName = "London"

            mainViewModel.onSearchQueryChanged(cityName)

            coEvery {
                weatherRepository.getWeather(
                    cityName,
                    null,
                    null,
                    any(),
                    any()
                )
            } returns mockk(relaxed = true)


            // Act
            mainViewModel.onSearchButtonClicked()

            // Assert
            coVerify {
                weatherRepository.getWeather(
                    cityName,
                    null,
                    null,
                    any(),
                    any()
                )
            }
        }

    @Test
    fun `when loadWeatherDataUsingLocation is called, it calls getWeather with the correct parameters`() =
        runBlocking {
            // Arrange
            val latitude = 51.5074
            val longitude = 0.1278
            coEvery {
                weatherRepository.getWeather(
                    null,
                    latitude,
                    longitude,
                    any(),
                    any()
                )
            } returns mockk(relaxed = true)

            // Act
            mainViewModel.loadWeatherDataUsingLocation(latitude, longitude)


            // Assert
            coVerify { // verify that the function was called
                weatherRepository.getWeather(
                    null,
                    latitude,
                    longitude,
                    any(),
                    any()
                )
            }

        }

    @Test
    fun `when loadWeatherData fails, it updates errorMessage`() = runBlocking {
        // Arrange
        val expectedErrorMessage = "Test Error Message"
        coEvery {
            weatherRepository.getWeather(
                cityName = any(),
                latitude = any(),
                longitude = any(),
                onComplete = any(),
                onError = captureLambda()
            )
        } coAnswers {
            lambda<(Int, String?) -> Unit>().captured.invoke(-1, expectedErrorMessage)
            flowOf<WeatherResponse?>(null)
        }

        // Act
        mainViewModel.loadWeatherData(cityName = "New York")

        // Wait for the onError lambda to be processed
        delay(100)

        // Assert
        val actualErrorMessage = mainViewModel.errorMessage.value
        assertEquals(expectedErrorMessage, actualErrorMessage)

    }


}