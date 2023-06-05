package com.cognizant.openweather.network

// ResponseResult is the base response structure for all the API calls
data class ResponseResult<T>(
    val data: T? = null,
    val responseCode: Int,
    val errorMessage: String?
) {
    companion object {
        fun <T> success(data: T, responseCode: Int): ResponseResult<T> {
            return ResponseResult(data, responseCode, null)
        }

        fun <T> error(errorMessage: String?, responseCode: Int): ResponseResult<T> {
            return ResponseResult(null, responseCode, errorMessage)
        }
    }
}