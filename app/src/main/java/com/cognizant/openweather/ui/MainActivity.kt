package com.cognizant.openweather.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cognizant.openweather.ui.MainActivity.Companion.SEARCH_SCREEN
import com.cognizant.openweather.ui.MainActivity.Companion.WEATHER_SCREEN
import com.cognizant.openweather.ui.screens.search.SearchScreen
import com.cognizant.openweather.ui.screens.weather.WeatherScreen
import com.example.compose.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    WeatherAppNavHost()
                }
            }
        }
    }

    companion object {
        const val SEARCH_SCREEN = "search"
        const val WEATHER_SCREEN = "weather"
    }
}

@Composable
fun WeatherAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = SEARCH_SCREEN
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        composable(WEATHER_SCREEN) {

            WeatherScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
        composable(SEARCH_SCREEN) {

            SearchScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
    }

}

