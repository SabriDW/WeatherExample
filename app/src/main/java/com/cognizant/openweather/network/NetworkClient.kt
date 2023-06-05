package com.cognizant.openweather.network

import com.squareup.moshi.Moshi
import okio.IOException
import retrofit2.Response
import javax.inject.Inject

/** a unified interface for executing network requests and handling responses
 */
class NetworkClient @Inject constructor(
    private val moshi: Moshi
) {

    // a generic function that executes the API call and handles the response
    suspend fun <T> execute(call: suspend () -> Response<T>): ResponseResult<T> {
        return try {
            processApiResponse(call())
        } catch (e: IOException) { // Retrofit throws IOException when there is no internet connection
            ResponseResult.error("No internet connection", -1)
        } catch (e: Exception) { // Catch any other exceptions
            ResponseResult.error(e.message, -1)
        }
    }


    // a generic function that handles the response from the API call
    private fun <T> processApiResponse(
        response: Response<T>
    ): ResponseResult<T> {
        if (response.isSuccessful) {
            response.body()?.let {
                return ResponseResult.success(it, response.code())
            } ?: run {
                return ResponseResult.error("API did not return any data", response.code())
            }
        } else {
            var error: BaseErrorResponse? = null
            response.errorBody()?.let {
                try {
                    error = moshi.adapter(BaseErrorResponse::class.java).fromJson(it.string())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return ResponseResult.error(error?.message ?: "Unknown error", response.code())
        }
    }

}