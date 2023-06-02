package com.cognizant.openweather.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cognizant.openweather.R
import com.cognizant.openweather.network.weather.Main
import com.cognizant.openweather.network.weather.Weather
import com.cognizant.openweather.network.weather.WeatherResponse
import com.cognizant.openweather.network.weather.Wind
import com.cognizant.openweather.ui.MainActivity.Companion.SEARCH_SCREEN
import com.cognizant.openweather.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: MainViewModel,
    navController: NavHostController
) {

    // hoist the states & events up
    val isLoading by viewModel.isLoading.collectAsState(false)
    val errorMessage by viewModel.errorMessage.collectAsState(null)
    val weatherData by viewModel.weatherData.collectAsState(null)


    // navigate back to search screen if weather data is null
    LaunchedEffect(weatherData) {
        if (weatherData == null) {
            viewModel.currentScreen.value = SEARCH_SCREEN
            navController.navigate(SEARCH_SCREEN) {
                popUpTo(navController.graph.startDestDisplayName) { inclusive = true }
            }
        }
    }

    weatherData?.let { weatherData ->

        Column(
            Modifier
                .fillMaxSize()
                .testTag("weather_screen_container")
        ) {

            TopAppBar(
                title = {
                    Text(text = "${weatherData.name}, ${weatherData.sys.country}")
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    Button(
                        modifier = Modifier.testTag("change_location_button"),
                        onClick = {
                            viewModel.currentScreen.value = SEARCH_SCREEN
                            navController.navigate(SEARCH_SCREEN)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(text = stringResource(R.string.change_location))
                    }
                }
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                WeatherOverview(weatherData = weatherData)

            }
        }
    }
}

@Composable
fun WeatherOverview(weatherData: WeatherResponse) {

    Card(Modifier.fillMaxWidth()) {

        Column(Modifier.padding(vertical = 16.dp)) {

            val weather = weatherData.weather.getOrNull(0)
            val main = weatherData.main
            val wind = weatherData.wind
            val visibility = weatherData.visibility


            if (weather != null && main != null) {
                MainWeatherInfo(weather = weather, main = main)
            }


            WeatherDetails(
                weather = weather,
                main = main,
                wind = wind,
                visibility = visibility
            )


        }

    }

}


@Composable
private fun MainWeatherInfo(weather: Weather, main: Main) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            modifier = Modifier
                .padding(end = 16.dp)
                .size(120.dp),
            model = weather.getIconUrl(),
            contentDescription = null,
        )

        Column {
            Text(
                text = weather.main,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "${main.temp}°C",
                style = MaterialTheme.typography.headlineLarge
            )

        }
    }
}

@Composable
fun WeatherDetails(
    weather: Weather?,
    main: Main?,
    wind: Wind?,
    visibility: Int
) {

    val textModifier = Modifier.padding(bottom = 8.dp)
    val textStyle = MaterialTheme.typography.bodyLarge

    Row(Modifier.padding(16.dp)) {
        Column(Modifier.weight(1f)) {
            Text(
                modifier = textModifier,
                text = "Feels like: ${main?.feelsLike}°C",
                style = textStyle
            )

            Text(
                modifier = textModifier,
                text = "Low: ${main?.tempMin}°C",
                style = textStyle
            )

            Text(
                modifier = textModifier,
                text = "High: ${main?.tempMax}°C",
                style = textStyle
            )

        }

        Column(Modifier.weight(1f)) {
            Text(
                modifier = textModifier,
                text = "Wind: ${wind?.speed}m/s, ${wind?.deg}°",
                style = textStyle
            )

            Text(
                modifier = textModifier,
                text = "Visibility: ${visibility / 1000}km",
                style = textStyle
            )

            Text(
                modifier = textModifier,
                text = "Humidity: ${main?.humidity}%",
                style = textStyle
            )
        }
    }


}
