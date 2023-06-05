package com.cognizant.openweather.ui.screens.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cognizant.openweather.R
import com.cognizant.openweather.network.currentweather.WeatherResponse
import com.cognizant.openweather.ui.MainActivity.Companion.SEARCH_SCREEN

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    navController: NavHostController
) {

    // hoist the states & events up
    val weatherData = viewModel.weatherData
    val units = viewModel.units

    val navigateToSearchScreen: (keepInBackStack: Boolean) -> Unit = { keepInBackStack ->
        navController.navigate(SEARCH_SCREEN) {
            if (!keepInBackStack) // pop this screen from back stack
                navController.popBackStack()
        }
    }

    // navigate back to search screen if weather data is null
    LaunchedEffect(weatherData) {
        if (weatherData == null) {
            navigateToSearchScreen(false)
        }
    }

    weatherData?.let { weatherData ->

        Column(
            Modifier
                .fillMaxSize()
                .testTag("weather_screen_container")
        ) {

            TopBar(
                cityName = weatherData.name,
                countryName = weatherData.sys.country,
                onChangeLocationClicked = { navigateToSearchScreen(true) }
            )

            WeatherBody(
                weatherData = weatherData,
                units = units
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    cityName: String,
    countryName: String,
    onChangeLocationClicked: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = "$cityName, $countryName")
        },
        actions = {
            Button(
                modifier = Modifier.testTag("change_location_button"),
                onClick = onChangeLocationClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(text = stringResource(R.string.change_location))
            }
        }
    )
}

@Composable
private fun WeatherBody(weatherData: WeatherResponse, units: Units) {
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        item {
            WeatherOverviewCard(
                iconUrl = weatherData.weather.firstOrNull()?.getIconUrl() ?: "N/A",
                weatherDescription = weatherData.weather.firstOrNull()?.main ?: "N/A",
                temperature = weatherData.main?.temp?.toInt().toString(),
                feelsLike = weatherData.main?.feelsLike?.toInt().toString(),
                units = units
            )
        }

        item {
            WeatherDetails(
                minTemp = weatherData.main?.tempMin?.toInt().toString(),
                maxTemp = weatherData.main?.tempMax?.toInt().toString(),
                windSpeed = weatherData.wind?.speed.toString(),
                windDirection = weatherData.wind?.deg.toString(),
                visibility = (weatherData.visibility / 1000).toString(),
                humidity = weatherData.main?.humidity.toString(),
                pressure = weatherData.main?.pressure.toString(),
                units = units
            )
        }
    }
}

@Composable
fun WeatherOverviewCard(
    iconUrl: String,
    weatherDescription: String,
    temperature: String,
    feelsLike: String,
    units: Units
) {

    Card(Modifier.fillMaxWidth()) {

        Column(Modifier.padding(vertical = 16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(120.dp),
                    model = iconUrl,
                    contentDescription = null,
                )

                Column {
                    Text(
                        text = weatherDescription,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = "$temperature${units.temperature}",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        text = stringResource(
                            R.string.feels_like_placeholder,
                            feelsLike,
                            units.temperature
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )

                }
            }

        }
    }
}

@Composable
fun WeatherDetails(
    minTemp: String,
    maxTemp: String,
    windSpeed: String,
    windDirection: String,
    visibility: String,
    humidity: String,
    pressure: String,
    units: Units,
) {

    val textStyle = MaterialTheme.typography.bodyLarge

    Row(Modifier.padding(top = 16.dp)) {
        Card(
            Modifier
                .weight(0.4f)
                .padding(end = 8.dp)
        ) {

            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = stringResource(R.string.low_placeholder, minTemp, units.temperature),
                    style = textStyle
                )

                Text(
                    text = stringResource(R.string.high_placeholder, maxTemp, units.temperature),
                    style = textStyle
                )

                Text(
                    text = stringResource(R.string.humidity_placeholder, humidity),
                    style = textStyle
                )

            }

        }

        Card(
            Modifier
                .weight(0.6f)
                .padding(start = 8.dp)
        ) {

            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = stringResource(
                        R.string.wind_speed_placeholder,
                        windSpeed,
                        units.windSpeed
                    ),
                    style = textStyle
                )

                Text(
                    text = stringResource(R.string.wind_direction_placeholder, windDirection),
                    style = textStyle
                )

                Text(
                    text = stringResource(
                        R.string.visibility_placeholder,
                        visibility,
                        units.distance
                    ),
                    style = textStyle
                )

                Text(
                    text = stringResource(R.string.pressure_placeholder, pressure, units.pressure),
                    style = textStyle
                )
            }

        }
    }


}
