package com.cognizant.openweather.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognizant.openweather.data.prefs.SearchPreferences
import com.cognizant.openweather.data.repositories.LocationRepositoryImpl
import com.cognizant.openweather.data.repositories.WeatherRepositoryImpl
import com.cognizant.openweather.network.currentweather.WeatherResponse
import com.cognizant.openweather.ui.MainActivity.Companion.SEARCH_SCREEN
import com.cognizant.openweather.ui.MainActivity.Companion.WEATHER_SCREEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val weatherRepository: WeatherRepositoryImpl,
    private val locationRepository: LocationRepositoryImpl,
    private val searchPreferences: SearchPreferences
) : ViewModel() {

    //region UI State & variables

    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val currentScreen: MutableStateFlow<String> = MutableStateFlow(SEARCH_SCREEN)
    val weatherData: MutableStateFlow<WeatherResponse?> = MutableStateFlow(null)

    // search query with the last saved value from shared preferences as the initial value
    val searchQuery: MutableStateFlow<String> = MutableStateFlow(
        searchPreferences.getSearchQuery() ?: ""
    )

    // used to check if data has already being loading to avoid using the location repository twice
    var locationAlreadyLoaded: Boolean = false


    //endregion

    init {

        // if the weather repository has cached weather data, skip loading
        // this is to avoid loading data twice when the user navigates back to the search screen
        if (weatherRepository.getCachedWeather() != null) {
            locationAlreadyLoaded = true
        } else {
            // directly load weather data if there is a saved search query or GPS permission is granted
            if (searchQuery.value.isNotEmpty()) {
                loadWeatherDataUsingSearchQuery()
            } else {
                loadGPSCoordinatesAndWeatherInfo()
            }
        }
    }

    //region UI Events
    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
        errorMessage.value = null // clear error message when the search query changes
    }

    fun onSearchButtonClicked() {
        loadWeatherDataUsingSearchQuery()
    }

    fun loadGPSCoordinatesAndWeatherInfo(
    ) {

        // if data has already being loaded using the location repository (e.g., when permission is first granted), do nothing
        if (locationAlreadyLoaded) {
            return
        }

        // if there is a saved search query, do nothing as searched query takes precedence
        if (!searchPreferences.getSearchQuery().isNullOrEmpty()) {
            return
        }

        // get the last known location from the location repository
        locationRepository.getLastLocation()?.let { task ->

            // set locationAlreadyLoaded to true to avoid using the location repository twice
            locationAlreadyLoaded = true

            isLoading.value = true // show loading indicator

            task.addOnCompleteListener { // hide loading indicator when the task completes
                isLoading.value = false
            }

            task.addOnSuccessListener { location ->
                loadWeatherDataUsingLocation(location.latitude, location.longitude)
            }
        }
    }
    //endregion

    private fun loadWeatherDataUsingLocation(
        latitude: Double,
        longitude: Double
    ) {
        loadWeatherData(latitude = latitude, longitude = longitude)
    }

    private fun loadWeatherDataUsingSearchQuery() {
        loadWeatherData(searchQuery.value)
    }

    private fun loadWeatherData(
        cityName: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            isLoading.value = true

            weatherRepository.getWeather(
                cityName = cityName,
                latitude = latitude,
                longitude = longitude,
            ).collect { weatherResponse ->

                isLoading.value = false // hide loading indicator

                weatherResponse.data?.let {

                    weatherData.emit(it)

                    // save search query to shared preferences only if the API call was successful,
                    searchPreferences.saveSearchQuery(searchQuery.value)

                    // switch to the weather screen
                    navigateToWeatherScreen()
                }

                // handle error message if there is one
                weatherResponse.errorMessage?.let {
                    handleErrorMessage(it)
                }


            }
        }

    }

    private fun navigateToWeatherScreen() {
        currentScreen.value = WEATHER_SCREEN
    }

    private fun handleErrorMessage(message: String) {
        errorMessage.value = message
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



