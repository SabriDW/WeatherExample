package com.cognizant.openweather.data.repositories

import com.cognizant.openweather.network.ResponseResult
import com.cognizant.openweather.network.currentweather.WeatherClient
import com.cognizant.openweather.network.currentweather.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherClient: WeatherClient,
) : WeatherRepository {


    private var weatherResponseCache: WeatherResponse? = null

    // get weather either by city name or lat/long
    override suspend fun getWeather(
        cityName: String?,
        latitude: Double?,
        longitude: Double?,
    ) = flow {

        // check if both city name and lat/long are null, if so, emit error and return
        if (cityName == null && (latitude == null || longitude == null)) {
            emit(
                ResponseResult<WeatherResponse>(
                    null,
                    -1,
                    "City name or lat/long must be provided"
                )
            )
            return@flow
        }

        val weatherResponse =
            if (latitude != null && longitude != null) // if lat/long are set, use them to get weather
                weatherClient.getWeatherByLatLng(latitude, longitude)
            else // otherwise, use city name
                weatherClient.getWeatherByCityName(cityName!!) // !! is safe here because we check if cityName is null above

        emit(weatherResponse)

        // cache the response, so we can use it later without making another network call
        weatherResponseCache = weatherResponse.data

    }.flowOn(Dispatchers.IO)

    // TODO: This is a temporary solution for demo purposes. We should use a database to cache the weather response.
    override fun getCachedWeather() = weatherResponseCache

}
