package com.cognizant.openweather.network.currentweather

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object MockWeatherResponse {

    fun getMockWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            code = 200,
            base = "stations",
            clouds = Clouds(all = 75),
            coord = Coordinates(lat = 51.5085, lon = -0.1257),
            dt = 1685649375,
            id = 2643743,
            main = Main(
                temp = 13.27,
                feelsLike = 12.61,
                humidity = 75,
                pressure = 1024,
                tempMax = 11.79,
                tempMin = 14.64
            ),
            name = "London",
            sys = Sys(
                country = "GB",
                id = 268730,
                sunrise = 1685591368,
                sunset = 1685650045,
                type = 2
            ),
            timezone = 3600,
            visibility = 10000,
            weather = listOf(
                Weather(
                    description = "overcast clouds",
                    icon = "04n",
                    id = 804,
                    main = "Clouds"
                )
            ),
            wind = Wind(
                deg = 230,
                speed = 1.54
            )
        )
    }

    suspend fun getMockWeatherResponseFlow(): Flow<WeatherResponse?> {
        return flow {
            emit(getMockWeatherResponse())
        }
    }
}