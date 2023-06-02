package com.cognizant.openweather.data.repositories

import com.cognizant.openweather.network.weather.WeatherClient
import com.cognizant.openweather.network.weather.WeatherErrorResponse
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherClient: WeatherClient,
    private val moshi: Moshi
) : WeatherRepository {

    // get weather either by city name or lat/long
    override suspend fun getWeather(
        cityName: String?,
        latitude: Double?,
        longitude: Double?,
        onComplete: () -> Unit,
        onError: (Int, String?) -> Unit
    ) = flow {

        // check if both city name and lat/long are null, if so, emit error
        if (cityName == null && (latitude == null || longitude == null)) {
            onError(-1, "Invalid input")
            return@flow
        }

        val weatherResponse =
            if (latitude != null && longitude != null) // if lat/long are set, use them to get weather
                weatherClient.getWeatherByLatLng(latitude, longitude)
            else // otherwise, use city name
                weatherClient.getWeatherByCityName(cityName!!) // !! is safe here because we check if cityName is null above

        // check if response is successful and body is not null
        if (weatherResponse.isSuccessful && weatherResponse.body() != null) {
            emit(weatherResponse.body()) // emit the body
            onComplete()
        } else { // otherwise, emit error

            var errorMessage: String? = null // default error message

            try { // try to parse error body
                weatherResponse.errorBody()?.string()?.let { errorBodyString ->
                    val adapter = moshi.adapter(WeatherErrorResponse::class.java)
                    adapter.fromJson(errorBodyString)?.let { errorResponse ->
                        errorMessage = errorResponse.message
                    }
                }

            } catch (e: JsonDataException) {
                e.printStackTrace()
            }

            // pass error code and message to onError callback
            onError(weatherResponse.code(), errorMessage)
        }
    }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)

}
