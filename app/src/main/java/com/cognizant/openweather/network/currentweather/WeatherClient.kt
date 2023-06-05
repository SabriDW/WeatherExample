package com.cognizant.openweather.network.currentweather

import com.cognizant.openweather.network.NetworkClient
import com.cognizant.openweather.network.ResponseResult
import javax.inject.Inject
import javax.inject.Named

class WeatherClient @Inject constructor(
    private val weatherService: WeatherService,
    private val networkClient: NetworkClient,
    @Named("apiKey") private val apiKey: String
) {

    suspend fun getWeatherByCityName(cityName: String): ResponseResult<WeatherResponse> {
        return networkClient.execute {
            weatherService.getWeatherByCityName(
                cityName,
                UNITS,
                apiKey
            )
        }
    }

    suspend fun getWeatherByLatLng(lat: Double, lng: Double): ResponseResult<WeatherResponse> {
        return networkClient.execute {
            weatherService.getWeatherByLatLng(
                lat,
                lng,
                UNITS,
                apiKey
            )
        }
    }

    companion object {
        private const val UNITS = "metric" // TODO: make this configurable by the user
    }

}