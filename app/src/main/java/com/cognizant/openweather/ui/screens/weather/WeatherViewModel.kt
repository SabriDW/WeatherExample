package com.cognizant.openweather.ui.screens.weather

import androidx.lifecycle.ViewModel
import com.cognizant.openweather.data.repositories.WeatherRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Weather screen
 * A bare-bones ViewModel that only exposes the weather data from the repository since
 * the Weather screen is a read-only screen
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    weatherRepository: WeatherRepositoryImpl
) : ViewModel() {

    val weatherData = weatherRepository.getCachedWeather()
    val units = Units(
        type = "metric",
        temperature = "°C",
        windSpeed = "m/s",
        pressure = "hPa",
        distance = "km",
    ) // TODO: load from settings

}

data class Units(
    val type: String,
    val temperature: String,
    val windSpeed: String,
    val pressure: String,
    val distance: String,
)
