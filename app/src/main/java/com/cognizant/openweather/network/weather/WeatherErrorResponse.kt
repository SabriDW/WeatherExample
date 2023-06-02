package com.cognizant.openweather.network.weather

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherErrorResponse(
    @Json(name = "cod")
    val code: Int,
    @Json(name = "message")
    val message: String? = null
)