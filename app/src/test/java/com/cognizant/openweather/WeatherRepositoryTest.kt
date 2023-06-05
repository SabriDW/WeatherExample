package com.cognizant.openweather

import com.cognizant.openweather.data.repositories.WeatherRepositoryImpl
import com.cognizant.openweather.network.ResponseResult
import com.cognizant.openweather.network.currentweather.WeatherClient
import com.cognizant.openweather.network.currentweather.WeatherResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

/**
 * This is a test class for [WeatherRepositoryImpl].
 */
class WeatherRepositoryTest {

    private val weatherClient: WeatherClient = mockk(relaxed = true)
    private val weatherRepository = WeatherRepositoryImpl(weatherClient)
    private val mockedResponse: ResponseResult<WeatherResponse> = mockk()
    private val mockedBody: WeatherResponse = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        coEvery { mockedResponse.data } returns mockedBody
    }

    @Test
    fun `when getWeather is called with a cityName, it calls weatherClient's getWeatherByCityName`() =
        runTest {
            // Arrange
            val cityName = "New York"
            coEvery { weatherClient.getWeatherByCityName(cityName) } returns mockedResponse


            // Act
            weatherRepository.getWeather(cityName, null, null).first()

            // Assert
            coVerify {
                weatherClient.getWeatherByCityName(cityName)
            }
        }

    @Test
    fun `when getWeather is called with a latitude and longitude, it calls weatherClient's getWeatherByLatLng`() =
        runTest {
            // Arrange
            val latitude = 40.7128
            val longitude = 74.0060

            coEvery { weatherClient.getWeatherByLatLng(latitude, longitude) } returns mockedResponse

            // Act
            weatherRepository.getWeather(null, latitude, longitude).first()

            // Assert
            coVerify {
                weatherClient.getWeatherByLatLng(latitude, longitude)
            }
        }

    @Test
    fun `when getWeather is called with no parameters, it returns an error`() = runTest {

        // Act
        val result = weatherRepository.getWeather(null, null, null).first()

        // Assert
        assert(result.data == null)
        assert(result.responseCode == -1)
    }

    @Test
    fun `when getWeather is called with proper parameters and result is successful, the data is cached`() =
        runTest {
            // Arrange
            val cityName = "New York"
            coEvery { weatherClient.getWeatherByCityName(cityName) } returns mockedResponse

            // Act
            weatherRepository.getWeather(cityName, null, null).first()

            // Assert
            assert(weatherRepository.getCachedWeather() == mockedBody)
        }

}