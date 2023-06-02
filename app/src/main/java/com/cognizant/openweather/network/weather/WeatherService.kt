package com.cognizant.openweather.network.weather

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather")
    suspend fun getWeatherByCityName(
        @Query("q") cityName: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

    @GET("weather")
    suspend fun getWeatherByLatLng(
        @Query("lat") lat: Double,
        @Query("lon") lng: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

}