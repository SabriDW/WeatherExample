package com.cognizant.openweather.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cognizant.openweather.ui.MainActivity.Companion.SEARCH_SCREEN
import com.cognizant.openweather.ui.MainActivity.Companion.WEATHER_SCREEN
import com.cognizant.openweather.ui.screens.SearchScreen
import com.cognizant.openweather.ui.common.theme.WeatherAppTheme
import com.cognizant.openweather.ui.screens.WeatherScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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
    viewModel: MainViewModel = viewModel(),
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
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(SEARCH_SCREEN) {

            SearchScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }

}

