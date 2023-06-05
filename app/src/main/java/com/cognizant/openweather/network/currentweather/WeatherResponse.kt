package com.cognizant.openweather.network.currentweather

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "cod")
    val code: Int,
    @Json(name = "base")
    val base: String,
    @Json(name = "clouds")
    val clouds: Clouds?,
    @Json(name = "coord")
    val coord: Coordinates?,
    @Json(name = "dt")
    val dt: Int?,
    @Json(name = "id")
    val id: Int?,
    @Json(name = "main")
    val main: Main?,
    @Json(name = "name")
    val name: String,
    @Json(name = "sys")
    val sys: Sys,
    @Json(name = "timezone")
    val timezone: Int,
    @Json(name = "visibility")
    val visibility: Int,
    @Json(name = "weather")
    val weather: List<Weather>,
    @Json(name = "wind")
    val wind: Wind?
)

@JsonClass(generateAdapter = true)
data class Clouds(
    @Json(name = "all")
    val all: Int
)

@JsonClass(generateAdapter = true)
data class Coordinates(
    @Json(name = "lat")
    val lat: Double,
    @Json(name = "lon")
    val lon: Double
)

@JsonClass(generateAdapter = true)
data class Main(
    @Json(name = "feels_like")
    val feelsLike: Double,
    @Json(name = "humidity")
    val humidity: Int,
    @Json(name = "pressure")
    val pressure: Int,
    @Json(name = "temp")
    val temp: Double,
    @Json(name = "temp_max")
    val tempMax: Double,
    @Json(name = "temp_min")
    val tempMin: Double
)

@JsonClass(generateAdapter = true)
data class Sys(
    @Json(name = "country")
    val country: String,
    @Json(name = "id")
    val id: Int?,
    @Json(name = "sunrise")
    val sunrise: Int,
    @Json(name = "sunset")
    val sunset: Int,
    @Json(name = "type")
    val type: Int?
)

@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "description")
    val description: String,
    @Json(name = "icon")
    val icon: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "main")
    val main: String
) {

    fun getIconUrl(): String {
        return "https://openweathermap.org/img/wn/$icon@2x.png"
    }
}

@JsonClass(generateAdapter = true)
data class Wind(
    @Json(name = "deg")
    val deg: Int,
    @Json(name = "speed")
    val speed: Double
)