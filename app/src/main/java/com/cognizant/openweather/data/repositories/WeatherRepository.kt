package com.cognizant.openweather.data.repositories

import com.cognizant.openweather.network.ResponseResult
import com.cognizant.openweather.network.currentweather.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getWeather(
        cityName: String?,
        latitude: Double?,
        longitude: Double?,
    ): Flow<ResponseResult<WeatherResponse>>

    fun getCachedWeather(): WeatherResponse?

}