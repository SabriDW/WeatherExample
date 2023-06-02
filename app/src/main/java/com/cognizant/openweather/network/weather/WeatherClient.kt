package com.cognizant.openweather.network.weather

import retrofit2.Response
import javax.inject.Inject

class WeatherClient @Inject constructor(
    private val weatherService: WeatherService
) {

    suspend fun getWeatherByCityName(cityName: String): Response<WeatherResponse> =
        weatherService.getWeatherByCityName(cityName, UNITS, API_KEY)

    suspend fun getWeatherByLatLng(lat: Double, lng: Double): Response<WeatherResponse> =
        weatherService.getWeatherByLatLng(lat, lng, UNITS, API_KEY)

    companion object {
        private const val API_KEY = "0fc22c042cfb8daaf3b5c0b50f203ca3" // TODO: store private keys in a secure location
        private const val UNITS = "metric" // TODO: make this configurable by the user
    }

}