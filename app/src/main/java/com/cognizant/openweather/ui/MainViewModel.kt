package com.cognizant.openweather.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognizant.openweather.data.prefs.SearchPreferences
import com.cognizant.openweather.data.repositories.LocationRepositoryImpl
import com.cognizant.openweather.data.repositories.WeatherRepositoryImpl
import com.cognizant.openweather.network.weather.WeatherResponse
import com.cognizant.openweather.ui.MainActivity.Companion.SEARCH_SCREEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// for the sake of simplicity given time constraints, we are using a single view model for both screens
// in a production app, we would have separate view models for each screen and pass data between them using the repositories

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepositoryImpl,
    private val locationRepository: LocationRepositoryImpl,
    private val searchPreferences: SearchPreferences
) : ViewModel() {

    //region UI State & variables

    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)

    var firstLoaded =
        false // used to check if data has already being loading to avoid using the location repository twice


    // search query with the last saved value from shared preferences as the initial value
    val searchQuery: MutableStateFlow<String> =
        MutableStateFlow(searchPreferences.getSearchQuery() ?: "")

    // current screen with the search screen as the initial value
    val currentScreen: MutableStateFlow<String> = MutableStateFlow(SEARCH_SCREEN)

    // weather data with null as the initial value (not loaded yet)
    val weatherData: MutableStateFlow<WeatherResponse?> = MutableStateFlow(null)


    //endregion

    init { // load weather data when the view model is initialized

        if (searchQuery.value.isNotEmpty()) {
            loadWeatherDataUsingSearchQuery()
        } else {
            loadGPSCoordinatesAndWeatherInfo()
        }

        firstLoaded = true

    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
        errorMessage.value = null
    }

    fun onSearchButtonClicked() {
        loadWeatherDataUsingSearchQuery()
    }

    fun loadGPSCoordinatesAndWeatherInfo(
    ) {
        if (firstLoaded) { // if this is not the first time the view model is initialized, do nothing
            return
        }
        // get the last known location from the location repository
        locationRepository.getLastLocation()?.let { task ->
            isLoading.value = true // show loading indicator

            task.addOnCompleteListener { // hide loading indicator when the task completes
                isLoading.value = false
            }

            task.addOnSuccessListener { location ->
                loadWeatherDataUsingLocation(location.latitude, location.longitude)
            }

        }

    }

    fun loadWeatherDataUsingLocation(
        latitude: Double,
        longitude: Double
    ) {
        loadWeatherData(latitude = latitude, longitude = longitude)
    }

    private fun loadWeatherDataUsingSearchQuery() {
        loadWeatherData(searchQuery.value)
    }

    fun loadWeatherData(
        cityName: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ) {

        // Check that either city name or lat/long are provided, otherwise show error message and abort
        if (cityName == null && (latitude == null || longitude == null)) {
            errorMessage.value = "Invalid parameters"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            isLoading.emit(true)
            weatherRepository.getWeather(
                cityName = cityName,
                latitude = latitude,
                longitude = longitude,
                onComplete = {
                    isLoading.value = false
                },
                onError = { code, message ->
                    isLoading.value = false
                    handleErrorMessage(message, code)
                }
            ).collect { weatherResponse ->
                weatherData.emit(weatherResponse) // emit the weather data to the UI

                // switch to the weather screen
                navigateTo(MainActivity.WEATHER_SCREEN)

                // save search query to shared preferences only if the API call was successful
                weatherResponse?.let {
                    searchPreferences.saveSearchQuery(searchQuery.value)
                }
            }
        }

    }

    fun navigateTo(screen: String) {
        currentScreen.value = screen
    }

    private fun handleErrorMessage(message: String?, code: Int) {
        errorMessage.value = message
            ?: "Unknown with code $code" // TODO: map error message to more user friendly and localized strings
    }

//    UNUSED: This function is not used anywhere in the app but is included here to demonstrate the use of the Geocoder class
//    private fun convertLatLongToCityName(
//        lat: Double,
//        long: Double,
//        cityNameCallback: (String?) -> Unit
//    ) {
//        val geocoder = Geocoder(context, Locale.getDefault()) // to use the context, inject application context to view model using hilt
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            geocoder.getFromLocation(lat, long, 1) { addresses ->
//                cityNameCallback(addresses.getOrNull(0)?.locality)
//            }
//        }
//    }
}



