package com.cognizant.openweather.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// BaseErrorResponse is the base error response structure for all the API calls
@JsonClass(generateAdapter = true)
data class BaseErrorResponse(
    @Json(name = "cod")
    val code: Int,
    @Json(name = "message")
    val message: String? = null
)