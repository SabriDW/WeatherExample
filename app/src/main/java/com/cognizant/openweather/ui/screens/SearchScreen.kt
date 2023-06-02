package com.cognizant.openweather.ui.screens

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.cognizant.openweather.R
import com.cognizant.openweather.ui.MainActivity.Companion.SEARCH_SCREEN
import com.cognizant.openweather.ui.MainActivity.Companion.WEATHER_SCREEN
import com.cognizant.openweather.ui.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    navController: NavHostController
) {

    // hoist the states & events up
    val isLoading by viewModel.isLoading.collectAsState(false)
    val errorMessage by viewModel.errorMessage.collectAsState(null)
    val searchQuery by viewModel.searchQuery.collectAsState("")
    val weatherData by viewModel.weatherData.collectAsState(null)
    val currentScreen by viewModel.currentScreen.collectAsState()

    val onSearchQueryChangedListener = viewModel::onSearchQueryChanged
    val onSearchButtonClickedListener = viewModel::onSearchButtonClicked
    val loadGPSCoordinatesAndWeatherInfo = viewModel::loadGPSCoordinatesAndWeatherInfo

    // handle navigation to weather screen
    var isNavigatedToWeatherScreen by remember { mutableStateOf(false) }

    LaunchedEffect(weatherData, currentScreen) {
        if (currentScreen == WEATHER_SCREEN && weatherData != null && !isNavigatedToWeatherScreen) {
            isNavigatedToWeatherScreen = true

            navController.popBackStack() // pop previous weather screen from back stack
            navController.navigate(WEATHER_SCREEN) {
                navController.popBackStack() // pop search screen from back stack
            }
        }
    }

    Box(Modifier.testTag("search_screen_container")) {

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SearchBox(
                text = searchQuery,
                onTextChanged = onSearchQueryChangedListener,
                onActionClick = onSearchButtonClickedListener,
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }

        LoadingIndicator(isDisplayed = isLoading)

    }

    AccompanistPermission(
        loadGPSCoordinatesAndWeatherInfo = loadGPSCoordinatesAndWeatherInfo
    )

}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun AccompanistPermission(
    loadGPSCoordinatesAndWeatherInfo: () -> Unit
) {
    // request permission using Accompanist library
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    ) { isGranted ->
        if (isGranted) {
            loadGPSCoordinatesAndWeatherInfo()
        }
    }

    // for the first time, request permission directly if not granted yet
    LaunchedEffect(locationPermissionState) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        } else { // if permission is already granted, load the weather data only if it's not already loading
            loadGPSCoordinatesAndWeatherInfo()
        }
    }

    // if permission is not granted for the first time, show rationale dialog
    if (!locationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale)
        Dialog(onDismissRequest = { }) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {

                Text(
                    text = stringResource(R.string.dialog_location_permission_rationale),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // display cancel and allow buttons
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = { locationPermissionState.launchPermissionRequest() }
                    ) {
                        Text(text = stringResource(R.string.allow))
                    }
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    onActionClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .testTag("search_field"),
            value = text,
            onValueChange = onTextChanged,
            enabled = !isLoading, // disable the text field when loading
            label = {
                Text(
                    text = stringResource(R.string.enter_city_name),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            singleLine = true,
            // make keyboard action button to be search
            keyboardActions = KeyboardActions(
                onSearch = {
                    onActionClick()
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = androidx.compose.ui.text.input.ImeAction.Search
            ),
            isError = errorMessage != null,
            supportingText = {
                errorMessage?.let {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        // add search button with icon
        IconButton(
            modifier = Modifier
                .padding(8.dp)
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.small
                )
                .padding(8.dp)
                .testTag("search_button"),
            onClick = onActionClick,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.acc_search_icon))
        }

    }
}

@Composable
fun LoadingIndicator(isDisplayed: Boolean) {
    if (isDisplayed) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("loading_indicator_container"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
