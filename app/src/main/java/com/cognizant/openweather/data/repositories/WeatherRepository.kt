package com.cognizant.openweather.data.repositories

import com.cognizant.openweather.network.weather.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface WeatherRepository {

    suspend fun getWeather(
        cityName: String?,
        latitude: Double?,
        longitude: Double?,
        onComplete: () -> Unit,
        onError: (Int, String?) -> Unit
    ): Flow<WeatherResponse?>


}