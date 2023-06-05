package com.cognizant.openweather.ui.screens.search

import android.Manifest
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.cognizant.openweather.R
import com.cognizant.openweather.ui.MainActivity.Companion.WEATHER_SCREEN
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
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


    LaunchedEffect(weatherData, currentScreen) {
        if (currentScreen == WEATHER_SCREEN && weatherData != null) {
            navController.popBackStack() // pop previous weather screen from back stack
            navController.navigate(WEATHER_SCREEN) {
                navController.popBackStack() // pop search screen from back stack
            }
        }
    }

    Box(Modifier.testTag("search_screen_container")) {

        val configuration = LocalConfiguration.current
        when (configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.search_illustration),
                        contentDescription = null
                    )

                    Instructions()

                    SearchBox(
                        text = searchQuery,
                        onTextChanged = onSearchQueryChangedListener,
                        onActionClick = onSearchButtonClickedListener,
                        isLoading = isLoading,
                        errorMessage = errorMessage
                    )
                }

            }

            Configuration.ORIENTATION_LANDSCAPE -> {

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.search_illustration),
                        contentDescription = null
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Instructions()

                        SearchBox(
                            text = searchQuery,
                            onTextChanged = onSearchQueryChangedListener,
                            onActionClick = onSearchButtonClickedListener,
                            isLoading = isLoading,
                            errorMessage = errorMessage
                        )

                    }
                }


            }
        }

        LoadingIndicator(isDisplayed = isLoading)

    }

    AccompanistPermission(
        loadGPSCoordinatesAndWeatherInfo = loadGPSCoordinatesAndWeatherInfo
    )

}

@Composable
private fun Instructions() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.how_to_search),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = """
                ${stringResource(id = R.string.how_to_search_01)}
                ${stringResource(id = R.string.how_to_search_02)}
                ${stringResource(id = R.string.how_to_search_03)}
            """.trimIndent()
        )
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

        IconButton(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.small
                )
                .testTag("search_button"),
            enabled = !isLoading, // disable the button when loading
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
        if (!locationPermissionState.status.isGranted && !locationPermissionState.status.shouldShowRationale) {
            locationPermissionState.launchPermissionRequest()
        } else if (locationPermissionState.status.isGranted) { // if permission is already granted, load the weather data only if it's not already loading
            loadGPSCoordinatesAndWeatherInfo()
        }
    }

    // if permission is not granted for the first time, show rationale dialog
    if (!locationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale) {

        var dialogDismissed by remember { mutableStateOf(false) }

        if (!dialogDismissed)
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
                            onClick = { dialogDismissed = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(text = stringResource(R.string.cancel))
                        }

                        Button(
                            onClick = { locationPermissionState.launchPermissionRequest() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(text = stringResource(R.string.okay))
                        }
                    }
                }
            }
    }
}
